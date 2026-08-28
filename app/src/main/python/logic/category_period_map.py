"""Static mapping from an activity's category to the Montessori sensitive
period(s) it most directly serves. Used to generate a short, human-readable
"why this fits right now" tag alongside a recommendation — not AI-generated,
just a lookup table a human can review and edit.

Keys must match the `category` values used in content.db (see
/tools/generate_content_db.py); values are sensitive-period names as stored
in the `sensitive_periods` table.
"""

CATEGORY_TO_PERIODS: dict[str, list[str]] = {
    "practical_life": ["Order", "Refinement of Movement", "Independence"],
    "sensory": ["Refinement of the Senses", "Small Objects"],
    "fine_motor": ["Small Objects", "Refinement of Movement"],
    "gross_motor": ["Movement", "Coordination of Movement"],
    "language": ["Language"],
    "practical_life_toddler": ["Order", "Independence"],
}


def match_reason(category: str, active_period_names: list[str]) -> str | None:
    """Return a short explanation string if `category` serves one of the
    child's currently active sensitive periods, else None.

    >>> match_reason("gross_motor", ["Movement", "Language"])
    'Supports the current sensitive period for Movement'
    >>> match_reason("language", ["Order"]) is None
    True
    """
    related = CATEGORY_TO_PERIODS.get(category, [])
    for period in active_period_names:
        if period in related:
            return f"Supports the current sensitive period for {period}"
    return None
