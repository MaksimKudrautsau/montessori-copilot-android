"""Deterministic, rule-based activity recommendations. No AI/ML — every
decision here is an explicit, readable rule so it can be unit-tested and
reasoned about like any other piece of business logic.

Expected activity dict shape (matches ActivityEntity in Kotlin):
    {
        "id": int,
        "title": str,
        "category": str,
        "age_min_months": int,
        "age_max_months": int,
        ... (other fields are passed through untouched)
    }
"""
from __future__ import annotations

from .category_period_map import match_reason


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
    age, balanced across categories rather than sorted by a single score.

    Rules, in order:
      1. Only activities whose [age_min_months, age_max_months] contains
         the child's current age are eligible.
      2. Anything in `dismissed_ids` is excluded (the parent already said
         no to it for this child).
      3. Results are drawn round-robin across categories so one category
         doesn't crowd out the rest, then each result is annotated with a
         `reason` string when it matches one of the child's currently
         active sensitive periods.
    """
    dismissed = set(dismissed_ids or [])
    active_periods = active_period_names or []

    eligible = [
        a for a in activities
        if _age_matches(a, child_age_months) and a["id"] not in dismissed
    ]

    by_category: dict[str, list[dict]] = {}
    for activity in eligible:
        by_category.setdefault(activity["category"], []).append(activity)

    # Stable category order = first-seen order in the input, so results are
    # deterministic given the same input (important for testability).
    category_order = list(by_category.keys())

    ordered: list[dict] = []
    while len(ordered) < limit and any(by_category[c] for c in category_order):
        for category in category_order:
            if not by_category[category]:
                continue
            activity = by_category[category].pop(0)
            reason = match_reason(activity["category"], active_periods)
            enriched = dict(activity)
            if reason:
                enriched["reason"] = reason
            ordered.append(enriched)
            if len(ordered) >= limit:
                break

    return ordered
