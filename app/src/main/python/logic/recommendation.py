"""Deterministic, rule-based activity recommendations. No AI/ML — every
decision here is an explicit, readable rule so it can be unit-tested and
reasoned about like any other piece of business logic.

Expected activity dict shape (locale-independent fields only — titles and
descriptions are resolved on the Kotlin side after these ids come back):

    {
        "id": int,
        "area": str,               # one of AREAS in generate_content_seed.py
        "age_min_months": int,
        "age_max_months": int,
        ...                        # other fields pass through untouched
    }
"""
from __future__ import annotations

from .category_period_map import matching_period


def _age_matches(activity: dict, age_months: int) -> bool:
    return activity["age_min_months"] <= age_months <= activity["age_max_months"]


def recommend(
    child_age_months: int,
    activities: list[dict],
    dismissed_ids: list[int] | None = None,
    active_period_names: list[str] | None = None,
    limit: int = 10,
) -> list[dict]:
    """Return up to `limit` recommended activities for a child of the given
    age, balanced across curriculum areas rather than ranked by one score.

    Rules, in order:
      1. Only activities whose [age_min_months, age_max_months] contains the
         child's current age are eligible.
      2. Anything in `dismissed_ids` is excluded — the parent already said no
         to it for this child.
      3. Results are drawn round-robin across areas so one area cannot crowd
         out the rest. With eight curriculum areas this matters more than it
         did with five.
      4. Each result is annotated with `reason_period` — the English name of a
         currently active sensitive period its area serves — when there is one.
         No user-facing sentence is built here; see matching_period().
    """
    dismissed = set(dismissed_ids or [])
    active_periods = active_period_names or []

    eligible = [
        a for a in activities
        if _age_matches(a, child_age_months) and a["id"] not in dismissed
    ]

    by_area: dict[str, list[dict]] = {}
    for activity in eligible:
        by_area.setdefault(activity["area"], []).append(activity)

    # Stable area order = first-seen order in the input, so results are
    # deterministic given the same input (important for testability).
    area_order = list(by_area.keys())

    ordered: list[dict] = []
    while len(ordered) < limit and any(by_area[a] for a in area_order):
        for area in area_order:
            if not by_area[area]:
                continue
            activity = by_area[area].pop(0)
            enriched = dict(activity)
            # The English period NAME, not a sentence — Kotlin resolves the
            # localised name and wording for display.
            period = matching_period(activity["area"], active_periods)
            if period:
                enriched["reason_period"] = period
            ordered.append(enriched)
            if len(ordered) >= limit:
                break

    return ordered
