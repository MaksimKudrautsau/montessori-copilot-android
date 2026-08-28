from logic.category_period_map import match_reason


def test_matches_first_related_period_present():
    reason = match_reason("gross_motor", ["Language", "Movement"])
    assert reason == "Supports the current sensitive period for Movement"


def test_returns_none_when_no_period_relates():
    assert match_reason("language", ["Movement", "Order"]) is None


def test_returns_none_for_unknown_category():
    assert match_reason("unknown_category", ["Movement"]) is None


def test_returns_none_when_no_active_periods():
    assert match_reason("practical_life", []) is None
