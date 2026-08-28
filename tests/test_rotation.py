from logic.rotation import rotation_status

TODAY = 20_000  # arbitrary epoch day for deterministic tests


def test_active_item_past_threshold_is_due():
    items = [{"id": 1, "status": "active", "date_placed_epoch_day": TODAY - 15}]
    result = rotation_status(items, today_epoch_day=TODAY, min_days_active=14)
    assert result[0]["due_for_rotation"] is True
    assert result[0]["days_on_shelf"] == 15


def test_active_item_under_threshold_is_not_due():
    items = [{"id": 1, "status": "active", "date_placed_epoch_day": TODAY - 5}]
    result = rotation_status(items, today_epoch_day=TODAY, min_days_active=14)
    assert result[0]["due_for_rotation"] is False


def test_boundary_is_inclusive():
    items = [{"id": 1, "status": "active", "date_placed_epoch_day": TODAY - 14}]
    result = rotation_status(items, today_epoch_day=TODAY, min_days_active=14)
    assert result[0]["due_for_rotation"] is True


def test_storage_item_is_never_due():
    items = [{"id": 1, "status": "storage", "date_placed_epoch_day": TODAY - 100}]
    result = rotation_status(items, today_epoch_day=TODAY)
    assert result[0]["due_for_rotation"] is False
    assert "days_on_shelf" not in result[0]


def test_custom_threshold_is_respected():
    items = [{"id": 1, "status": "active", "date_placed_epoch_day": TODAY - 10}]
    assert rotation_status(items, TODAY, min_days_active=7)[0]["due_for_rotation"] is True
    assert rotation_status(items, TODAY, min_days_active=30)[0]["due_for_rotation"] is False


def test_empty_list_returns_empty_list():
    assert rotation_status([], today_epoch_day=TODAY) == []
