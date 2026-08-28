from logic.recommendation import recommend

PRACTICAL_LIFE = {
    "id": 1, "title": "Pouring water", "category": "practical_life",
    "age_min_months": 12, "age_max_months": 24,
}
SENSORY = {
    "id": 2, "title": "Object permanence box", "category": "sensory",
    "age_min_months": 8, "age_max_months": 14,
}
GROSS_MOTOR = {
    "id": 3, "title": "Pull toy walking", "category": "gross_motor",
    "age_min_months": 10, "age_max_months": 18,
}
TOO_YOUNG = {
    "id": 4, "title": "Newborn mobile", "category": "sensory",
    "age_min_months": 0, "age_max_months": 3,
}
TOO_OLD = {
    "id": 5, "title": "Cursive writing", "category": "language",
    "age_min_months": 60, "age_max_months": 72,
}

ALL_ACTIVITIES = [PRACTICAL_LIFE, SENSORY, GROSS_MOTOR, TOO_YOUNG, TOO_OLD]


def test_filters_by_age_range():
    result = recommend(child_age_months=13, activities=ALL_ACTIVITIES)
    result_ids = {a["id"] for a in result}
    assert result_ids == {1, 2, 3}  # only the three matching 13 months


def test_excludes_dismissed_activities():
    result = recommend(
        child_age_months=13,
        activities=ALL_ACTIVITIES,
        dismissed_ids=[2],
    )
    result_ids = {a["id"] for a in result}
    assert 2 not in result_ids
    assert result_ids == {1, 3}


def test_balances_across_categories_round_robin():
    # Two practical_life activities vs one sensory: with limit=2 we should
    # get one of each category first, not both practical_life activities.
    second_practical_life = {
        "id": 6, "title": "Folding cloths", "category": "practical_life",
        "age_min_months": 12, "age_max_months": 24,
    }
    activities = [PRACTICAL_LIFE, second_practical_life, SENSORY]
    result = recommend(child_age_months=13, activities=activities, limit=2)
    categories = {a["category"] for a in result}
    assert categories == {"practical_life", "sensory"}


def test_annotates_reason_when_sensitive_period_matches():
    result = recommend(
        child_age_months=13,
        activities=[GROSS_MOTOR],
        active_period_names=["Movement"],
    )
    assert result[0]["reason"] == "Supports the current sensitive period for Movement"


def test_no_reason_when_no_period_matches():
    result = recommend(
        child_age_months=13,
        activities=[GROSS_MOTOR],
        active_period_names=["Language"],
    )
    assert "reason" not in result[0]


def test_empty_activities_returns_empty_list():
    assert recommend(child_age_months=13, activities=[]) == []


def test_respects_limit():
    activities = [
        {"id": i, "title": f"A{i}", "category": "practical_life",
         "age_min_months": 0, "age_max_months": 72}
        for i in range(20)
    ]
    result = recommend(child_age_months=13, activities=activities, limit=5)
    assert len(result) == 5
