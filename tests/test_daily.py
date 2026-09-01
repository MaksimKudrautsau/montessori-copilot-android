"""Tests for the daily loop.

The properties that matter to a parent are stability and rotation: the same
suggestion all day, a different one tomorrow. Those are asserted directly,
because getting them wrong makes the word "today" meaningless without ever
throwing an error.
"""
import pytest

from logic.daily import daily_focus, upcoming_changes

ACTIVITIES = [
    {"id": 1, "area": "practical_life", "age_min_months": 12, "age_max_months": 24},
    {"id": 2, "area": "sensorial", "age_min_months": 12, "age_max_months": 24},
    {"id": 3, "area": "movement", "age_min_months": 12, "age_max_months": 24},
    {"id": 4, "area": "language", "age_min_months": 36, "age_max_months": 60},
]

DAY = 20_000


# --- daily_focus ------------------------------------------------------------

def test_same_child_same_day_gives_same_activity():
    """A parent opening the app twice before lunch must not see two different
    'today' suggestions."""
    first = daily_focus(1, DAY, 13, ACTIVITIES)
    second = daily_focus(1, DAY, 13, ACTIVITIES)
    assert first["id"] == second["id"]


def test_next_day_gives_a_different_activity():
    today = daily_focus(1, DAY, 13, ACTIVITIES)
    tomorrow = daily_focus(1, DAY + 1, 13, ACTIVITIES)
    assert today["id"] != tomorrow["id"]


def test_two_children_same_age_same_day_differ():
    """Two siblings shouldn't be handed identical suggestions — it reads as
    mechanical to a parent watching both."""
    a = daily_focus(1, DAY, 13, ACTIVITIES)
    b = daily_focus(2, DAY, 13, ACTIVITIES)
    assert a["id"] != b["id"]


def test_cycles_through_the_whole_eligible_pool():
    """Over consecutive days every eligible activity should appear, rather
    than a favourite few repeating."""
    seen = {daily_focus(1, DAY + offset, 13, ACTIVITIES)["id"] for offset in range(3)}
    assert seen == {1, 2, 3}


def test_only_age_appropriate_activities_are_chosen():
    for offset in range(10):
        chosen = daily_focus(1, DAY + offset, 13, ACTIVITIES)
        assert chosen["id"] != 4, "picked an activity outside the child's age range"


def test_dismissed_activities_are_never_chosen():
    for offset in range(10):
        chosen = daily_focus(1, DAY + offset, 13, ACTIVITIES, dismissed_ids=[1, 2])
        assert chosen["id"] == 3


def test_returns_none_when_nothing_is_eligible():
    assert daily_focus(1, DAY, 99, ACTIVITIES) is None
    assert daily_focus(1, DAY, 13, []) is None


def test_annotates_period_when_it_matches():
    chosen = daily_focus(1, DAY, 13, ACTIVITIES, active_period_names=["Movement"])
    if chosen["area"] == "movement":
        assert chosen["reason_period"] == "Movement"


def test_reason_period_is_a_bare_name_not_a_sentence():
    """Same localisation contract as recommend(): no English sentences may
    cross the bridge."""
    for offset in range(6):
        chosen = daily_focus(
            1, DAY + offset, 13, ACTIVITIES,
            active_period_names=["Movement", "Language", "Order"],
        )
        if "reason_period" in chosen:
            assert len(chosen["reason_period"].split()) <= 4


def test_does_not_mutate_the_input():
    """The caller reuses the activity list; annotating must not leak into it."""
    activities = [dict(a) for a in ACTIVITIES]
    daily_focus(1, DAY, 13, activities, active_period_names=["Movement"])
    assert all("reason_period" not in a for a in activities)


# --- upcoming_changes -------------------------------------------------------

PERIODS = [
    {"name": "Movement", "age_min_months": 0, "age_max_months": 48},
    {"name": "Small objects", "age_min_months": 12, "age_max_months": 30},
    {"name": "Grace and courtesy", "age_min_months": 30, "age_max_months": 72},
]


def test_reports_newly_eligible_activities():
    result = upcoming_changes(35, 36, ACTIVITIES, PERIODS)
    assert result["newly_eligible_ids"] == [4]
    assert result["has_changes"] is True


def test_reports_a_period_starting():
    result = upcoming_changes(29, 30, ACTIVITIES, PERIODS)
    assert "Grace and courtesy" in result["periods_starting"]


def test_reports_a_period_ending():
    result = upcoming_changes(30, 31, ACTIVITIES, PERIODS)
    assert "Small objects" in result["periods_ending"]


def test_already_outgrown_activities_are_not_reported_as_new():
    """An activity the child has passed must never show up as 'new' — that
    would be actively misleading."""
    result = upcoming_changes(25, 26, ACTIVITIES, PERIODS)
    assert 1 not in result["newly_eligible_ids"]
    assert 2 not in result["newly_eligible_ids"]


def test_quiet_month_reports_no_changes():
    """Most months nothing changes; the UI should then show no banner at all
    rather than an empty one."""
    result = upcoming_changes(20, 21, ACTIVITIES, PERIODS)
    assert result["newly_eligible_ids"] == []
    assert result["periods_starting"] == []
    assert result["periods_ending"] == []
    assert result["has_changes"] is False


def test_next_age_is_echoed_back():
    assert upcoming_changes(13, 14, ACTIVITIES, PERIODS)["next_age_months"] == 14


@pytest.mark.parametrize("age", [0, 1, 12, 36, 71, 72])
def test_never_raises_across_the_supported_age_range(age):
    result = upcoming_changes(age, age + 1, ACTIVITIES, PERIODS)
    assert isinstance(result["newly_eligible_ids"], list)
