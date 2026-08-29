from logic.recommendation import recommend

PRACTICAL_LIFE = {
    "id": 1, "area": "practical_life",
    "age_min_months": 12, "age_max_months": 24,
}
SENSORIAL = {
    "id": 2, "area": "sensorial",
    "age_min_months": 8, "age_max_months": 14,
}
MOVEMENT = {
    "id": 3, "area": "movement",
    "age_min_months": 10, "age_max_months": 18,
}
TOO_YOUNG = {
    "id": 4, "area": "sensorial",
    "age_min_months": 0, "age_max_months": 3,
}
TOO_OLD = {
    "id": 5, "area": "language",
    "age_min_months": 60, "age_max_months": 72,
}

ALL_ACTIVITIES = [PRACTICAL_LIFE, SENSORIAL, MOVEMENT, TOO_YOUNG, TOO_OLD]


def test_filters_by_age_range():
    result = recommend(child_age_months=13, activities=ALL_ACTIVITIES)
    assert {a["id"] for a in result} == {1, 2, 3}


def test_excludes_dismissed_activities():
    result = recommend(
        child_age_months=13, activities=ALL_ACTIVITIES, dismissed_ids=[2]
    )
    ids = {a["id"] for a in result}
    assert 2 not in ids
    assert ids == {1, 3}


def test_balances_across_areas_round_robin():
    # Two practical_life vs one sensorial: with limit=2 we should get one of
    # each area, not both practical_life activities.
    second_practical_life = {
        "id": 6, "area": "practical_life",
        "age_min_months": 12, "age_max_months": 24,
    }
    activities = [PRACTICAL_LIFE, second_practical_life, SENSORIAL]
    result = recommend(child_age_months=13, activities=activities, limit=2)
    assert {a["area"] for a in result} == {"practical_life", "sensorial"}


def test_annotates_period_name_when_sensitive_period_matches():
    result = recommend(
        child_age_months=13,
        activities=[MOVEMENT],
        active_period_names=["Movement"],
    )
    # A bare period name, not a sentence — the display wording is localised
    # on the Kotlin side so Russian users don't get English text.
    assert result[0]["reason_period"] == "Movement"


def test_no_period_when_none_matches():
    result = recommend(
        child_age_months=13,
        activities=[MOVEMENT],
        active_period_names=["Language"],
    )
    assert "reason_period" not in result[0]


def test_reason_period_is_never_a_sentence():
    """Guards the localisation contract: anything sentence-like here would be
    untranslatable English leaking into the UI."""
    result = recommend(
        child_age_months=13, activities=[MOVEMENT], active_period_names=["Movement"],
    )
    assert " for " not in result[0]["reason_period"]
    assert len(result[0]["reason_period"].split()) <= 4


def test_empty_activities_returns_empty_list():
    assert recommend(child_age_months=13, activities=[]) == []


def test_respects_limit():
    activities = [
        {"id": i, "area": "practical_life",
         "age_min_months": 0, "age_max_months": 72}
        for i in range(20)
    ]
    result = recommend(child_age_months=13, activities=activities, limit=5)
    assert len(result) == 5


def test_all_eight_areas_are_represented_before_any_repeats():
    """With one activity per area, a limit of 8 should return all eight —
    the round-robin must not favour whichever area came first."""
    areas = [
        "practical_life", "sensorial", "language", "mathematics",
        "movement", "art_and_music", "grace_and_courtesy", "culture_and_nature",
    ]
    activities = [
        {"id": i, "area": area, "age_min_months": 0, "age_max_months": 72}
        for i, area in enumerate(areas)
    ]
    result = recommend(child_age_months=36, activities=activities, limit=8)
    assert {a["area"] for a in result} == set(areas)
