"""Exercises bridge.py exactly as Kotlin will call it: JSON string in,
JSON string out. This is the contract PythonBridge.kt relies on.
"""
import json

from logic.bridge import recommend_json, rotation_status_json


def test_recommend_json_round_trip():
    payload = {
        "child_age_months": 13,
        "activities": [
            {"id": 1, "title": "Pouring water", "category": "practical_life",
             "age_min_months": 12, "age_max_months": 24},
        ],
        "dismissed_ids": [],
        "active_period_names": [],
    }
    result = json.loads(recommend_json(json.dumps(payload)))
    assert len(result) == 1
    assert result[0]["title"] == "Pouring water"


def test_rotation_status_json_round_trip():
    payload = {
        "shelf_items": [{"id": 1, "status": "active", "date_placed_epoch_day": 100}],
        "today_epoch_day": 120,
        "min_days_active": 14,
    }
    result = json.loads(rotation_status_json(json.dumps(payload)))
    assert result[0]["due_for_rotation"] is True
