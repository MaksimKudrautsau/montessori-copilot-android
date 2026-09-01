"""The only module Kotlin calls directly (via Chaquopy). Keeps the
Kotlin<->Python boundary to a simple JSON-string-in / JSON-string-out
contract, so nothing on the Kotlin side needs to know Python object
internals, and nothing on the Python side needs to know Chaquopy exists.

See MontessoriApp app/src/main/java/.../logic/PythonBridge.kt for the
Kotlin-side caller.
"""
from __future__ import annotations

import json

from .daily import daily_focus, upcoming_changes
from .recommendation import recommend
from .rotation import rotation_status


def recommend_json(payload_json: str) -> str:
    """payload: {
        "child_age_months": int,
        "activities": [activity dict, ...],
        "dismissed_ids": [int, ...],
        "active_period_names": [str, ...],
        "limit": int (optional)
    }
    returns: JSON list of recommended activity dicts.
    """
    payload = json.loads(payload_json)
    result = recommend(
        child_age_months=payload["child_age_months"],
        activities=payload["activities"],
        dismissed_ids=payload.get("dismissed_ids", []),
        active_period_names=payload.get("active_period_names", []),
        limit=payload.get("limit", 10),
    )
    return json.dumps(result)


def rotation_status_json(payload_json: str) -> str:
    """payload: {
        "shelf_items": [shelf item dict, ...],
        "today_epoch_day": int,
        "min_days_active": int (optional)
    }
    returns: JSON list of shelf items annotated with due_for_rotation.
    """
    payload = json.loads(payload_json)
    kwargs = {}
    if "min_days_active" in payload:
        kwargs["min_days_active"] = payload["min_days_active"]
    result = rotation_status(
        shelf_items=payload["shelf_items"],
        today_epoch_day=payload["today_epoch_day"],
        **kwargs,
    )
    return json.dumps(result)


def daily_focus_json(payload_json: str) -> str:
    """payload: {
        "child_id": int,
        "day_number": int,          # LocalDate.toEpochDay()
        "child_age_months": int,
        "activities": [activity dict, ...],
        "dismissed_ids": [int, ...],
        "active_period_names": [str, ...]
    }
    returns: JSON object for the chosen activity, or "null" if none is eligible.
    """
    payload = json.loads(payload_json)
    result = daily_focus(
        child_id=payload["child_id"],
        day_number=payload["day_number"],
        child_age_months=payload["child_age_months"],
        activities=payload["activities"],
        dismissed_ids=payload.get("dismissed_ids", []),
        active_period_names=payload.get("active_period_names", []),
    )
    return json.dumps(result)


def upcoming_changes_json(payload_json: str) -> str:
    """payload: {
        "current_age_months": int,
        "next_age_months": int,
        "activities": [activity dict, ...],
        "periods": [{"name", "age_min_months", "age_max_months"}, ...]
    }
    returns: JSON object describing what changes at the milestone.
    """
    payload = json.loads(payload_json)
    result = upcoming_changes(
        current_age_months=payload["current_age_months"],
        next_age_months=payload["next_age_months"],
        activities=payload["activities"],
        periods=payload["periods"],
    )
    return json.dumps(result)
