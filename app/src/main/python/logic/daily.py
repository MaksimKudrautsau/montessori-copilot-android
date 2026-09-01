"""The daily loop: one focused suggestion per day, and what changes when the
child reaches their next month.

Both are deterministic — no randomness, no AI. Given the same child, the same
day and the same library, the answer is always the same. That matters for more
than testability: a parent who opens the app twice before lunch must not see
two different "today" suggestions, or the word stops meaning anything.

DATE ARITHMETIC IS DELIBERATELY NOT DONE HERE. Kotlin computes the child's age
in months and the days until their next monthly birthday using java.time, which
handles month lengths correctly. This module only decides *what changes* at a
given age — see PRD v0.5 E3.
"""
from __future__ import annotations

from .category_period_map import matching_period


def daily_focus(
    child_id: int,
    day_number: int,
    child_age_months: int,
    activities: list[dict],
    dismissed_ids: list[int] | None = None,
    active_period_names: list[str] | None = None,
) -> dict | None:
    """Pick the single activity to feature today, or None if nothing is eligible.

    Rotation, not random choice. Eligible activities are sorted by id and
    indexed by `(day_number + child_id)`, so:

      * the same child sees the same activity all day,
      * they see a different one tomorrow,
      * two children of the same age don't get the same suggestion on the
        same day, which would look mechanical to a parent with two children,
      * over time the whole eligible pool is cycled rather than a favourite
        few being repeated.

    `day_number` is the epoch day (Kotlin's LocalDate.toEpochDay()).
    """
    dismissed = set(dismissed_ids or [])
    eligible = sorted(
        (
            a for a in activities
            if a["age_min_months"] <= child_age_months <= a["age_max_months"]
            and a["id"] not in dismissed
        ),
        key=lambda a: a["id"],
    )
    if not eligible:
        return None

    chosen = dict(eligible[(day_number + child_id) % len(eligible)])
    period = matching_period(chosen["area"], active_period_names or [])
    if period:
        chosen["reason_period"] = period
    return chosen


def upcoming_changes(
    current_age_months: int,
    next_age_months: int,
    activities: list[dict],
    periods: list[dict],
) -> dict:
    """What becomes newly relevant when the child reaches `next_age_months`.

    Returns newly eligible activity ids, and sensitive periods that start or
    end at that age. This is the substance behind "Emily turns 14 months this
    week — here's what's changing".

    `periods` entries need `name` (English), `age_min_months`, `age_max_months`.

    An activity counts as newly eligible if it is out of range now and in range
    at the milestone — so an activity the child has already outgrown never
    appears as "new".
    """
    def in_range(item: dict, age: int) -> bool:
        return item["age_min_months"] <= age <= item["age_max_months"]

    newly_eligible = [
        a["id"] for a in activities
        if not in_range(a, current_age_months) and in_range(a, next_age_months)
    ]

    starting = [
        p["name"] for p in periods
        if not in_range(p, current_age_months) and in_range(p, next_age_months)
    ]
    ending = [
        p["name"] for p in periods
        if in_range(p, current_age_months) and not in_range(p, next_age_months)
    ]

    return {
        "next_age_months": next_age_months,
        "newly_eligible_ids": sorted(newly_eligible),
        "periods_starting": starting,
        "periods_ending": ending,
        # Lets the caller skip the banner entirely rather than showing an
        # "and here's what changes: nothing" card.
        "has_changes": bool(newly_eligible or starting or ending),
    }
