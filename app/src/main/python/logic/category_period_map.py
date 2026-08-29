"""Static mapping from a Montessori curriculum area to the sensitive period(s)
it most directly serves. Used to generate a short, human-readable "why this
fits right now" tag alongside a recommendation — not AI-generated, just a
lookup table a human can review and edit.

Keys MUST match the `area` values in tools/generate_content_seed.py (AREAS).
Values MUST match sensitive-period names in the English seed data — the app
resolves the localised name for display separately, so this table stays in
one language and is never translated.
"""

AREA_TO_PERIODS: dict[str, list[str]] = {
    "practical_life": [
        "Order",
        "Refinement of movement",
        "Independence",
        "Coordination of movement",
    ],
    "sensorial": [
        "Refinement of the senses",
        "Small objects",
    ],
    "language": [
        "Language",
    ],
    "mathematics": [
        "Refinement of the senses",
        "Order",
    ],
    "movement": [
        "Movement",
        "Coordination of movement",
        "Refinement of movement",
    ],
    "art_and_music": [
        "Refinement of the senses",
        "Refinement of movement",
    ],
    "grace_and_courtesy": [
        "Grace and courtesy",
        "Order",
    ],
    "culture_and_nature": [
        "Language",
        "Refinement of the senses",
    ],
}

# Kept as an alias so nothing silently breaks if an older import lingers.
CATEGORY_TO_PERIODS = AREA_TO_PERIODS


def matching_period(area: str, active_period_names: list[str]) -> str | None:
    """Return the NAME of the first active sensitive period that `area` serves,
    or None if it serves none.

    Deliberately returns a bare period name, not a sentence. The displayed
    wording ("Supports the current sensitive period for X") is assembled on the
    Kotlin side from a string resource, so a Russian user sees Russian — the
    matching stays in English because this table is keyed on English names,
    but nothing user-visible crosses the bridge.

    The order of `active_period_names` decides which match wins, so callers
    should pass periods in the order they want them preferred.

    >>> matching_period("movement", ["Movement", "Language"])
    'Movement'
    >>> matching_period("language", ["Order"]) is None
    True
    """
    related = AREA_TO_PERIODS.get(area, [])
    for period in active_period_names:
        if period in related:
            return period
    return None
