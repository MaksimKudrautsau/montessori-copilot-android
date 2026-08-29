import pytest

from logic.category_period_map import AREA_TO_PERIODS, matching_period


def test_matches_first_related_period_present():
    assert matching_period("movement", ["Language", "Movement"]) == "Movement"


def test_returns_none_when_no_period_relates():
    assert matching_period("language", ["Movement", "Order"]) is None


def test_returns_none_for_unknown_area():
    assert matching_period("unknown_area", ["Movement"]) is None


def test_returns_none_when_no_active_periods():
    assert matching_period("practical_life", []) is None


def test_new_areas_all_have_mappings():
    """The four areas added in P0 must not silently fall through to None."""
    for area in ("mathematics", "art_and_music", "grace_and_courtesy", "culture_and_nature"):
        assert AREA_TO_PERIODS.get(area), f"{area} has no sensitive-period mapping"


@pytest.mark.parametrize("area", list(AREA_TO_PERIODS))
def test_every_mapped_area_can_produce_a_reason(area):
    """Every area must match when one of its own periods is active —
    catches typos between this table and the seed data."""
    first_period = AREA_TO_PERIODS[area][0]
    assert matching_period(area, [first_period]) == first_period
