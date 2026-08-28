"""Shelf-rotation logic: decide which "active shelf" items are stale enough
to suggest rotating into storage. Pure date-arithmetic, no AI.

Expected shelf item dict shape (matches ShelfItemEntity in Kotlin):
    {
        "id": int,
        "status": "active" | "storage",
        "date_placed_epoch_day": int,
    }
"""
from __future__ import annotations

DEFAULT_MIN_DAYS_ACTIVE = 14


def rotation_status(
    shelf_items: list[dict],
    today_epoch_day: int,
    min_days_active: int = DEFAULT_MIN_DAYS_ACTIVE,
) -> list[dict]:
    """Return the input list with a `due_for_rotation` bool added to every
    item currently on the active shelf. Items already in storage are passed
    through unchanged (with `due_for_rotation: False`) since rotation only
    makes sense for things a child is currently engaging with.
    """
    results = []
    for item in shelf_items:
        enriched = dict(item)
        if item["status"] == "active":
            days_on_shelf = today_epoch_day - item["date_placed_epoch_day"]
            enriched["due_for_rotation"] = days_on_shelf >= min_days_active
            enriched["days_on_shelf"] = days_on_shelf
        else:
            enriched["due_for_rotation"] = False
        results.append(enriched)
    return results
