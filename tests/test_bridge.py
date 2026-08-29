"""Exercises bridge.py exactly as Kotlin calls it: JSON string in, JSON string
out. This is the contract PythonBridge.kt relies on.
"""
import json

from logic.bridge import recommend_json, rotation_status_json


def test_recommend_json_round_trip():
    payload = {
        "child_age_months": 13,
        "activities": [
            {"id": 1, "area": "practical_life",
             "age_min_months": 12, "age_max_months": 24},
        ],
        "dismissed_ids": [],
        "active_period_names": [],
    }
    result = json.loads(recommend_json(json.dumps(payload)))
    assert len(result) == 1
    assert result[0]["id"] == 1
    assert result[0]["area"] == "practical_life"


def test_recommend_json_carries_period_through():
    payload = {
        "child_age_months": 13,
        "activities": [
            {"id": 1, "area": "movement",
             "age_min_months": 12, "age_max_months": 24},
        ],
        "active_period_names": ["Movement"],
    }
    result = json.loads(recommend_json(json.dumps(payload)))
    assert result[0]["reason_period"] == "Movement"


def test_rotation_status_json_round_trip():
    payload = {
        "shelf_items": [{"id": 1, "status": "active", "date_placed_epoch_day": 100}],
        "today_epoch_day": 120,
        "min_days_active": 14,
    }
    result = json.loads(rotation_status_json(json.dumps(payload)))
    assert result[0]["due_for_rotation"] is True


def test_bridge_handles_unicode_safely():
    """Content is bilingual now; the JSON boundary must not mangle Cyrillic."""
    payload = {
        "child_age_months": 13,
        "activities": [
            {"id": 1, "area": "language", "title": "Переливание воды",
             "age_min_months": 12, "age_max_months": 24},
        ],
    }
    result = json.loads(recommend_json(json.dumps(payload)))
    assert result[0]["title"] == "Переливание воды"
