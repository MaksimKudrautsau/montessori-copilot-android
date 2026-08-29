"""Cross-checks the generated content library against the logic layer.

These are the tests that catch content/code drift — a renamed area or a
mistyped sensitive-period name would otherwise fail silently at runtime by
simply never producing a "why this fits now" reason.
"""
import json
from pathlib import Path

import pytest

from logic.category_period_map import AREA_TO_PERIODS

SEED_PATH = Path(__file__).resolve().parent.parent / "app/src/main/assets/content_seed.json"
LOCALES = ("en", "ru")


@pytest.fixture(scope="module")
def seed():
    return json.loads(SEED_PATH.read_text(encoding="utf-8"))


def test_seed_file_exists_and_is_current_schema(seed):
    assert seed["schemaVersion"] == 2


def test_every_activity_area_has_a_period_mapping(seed):
    unmapped = {a["area"] for a in seed["activities"] if a["area"] not in AREA_TO_PERIODS}
    assert not unmapped, f"areas present in content but missing from AREA_TO_PERIODS: {unmapped}"


def test_every_mapped_period_name_exists_in_seed(seed):
    """AREA_TO_PERIODS references sensitive periods by their English name.
    A typo here means the reason line never appears, with no error."""
    real_names = {
        t["name"] for t in seed["sensitivePeriodTexts"] if t["locale"] == "en"
    }
    referenced = {name for names in AREA_TO_PERIODS.values() for name in names}
    unknown = referenced - real_names
    assert not unknown, f"AREA_TO_PERIODS references non-existent periods: {unknown}"


def test_every_activity_has_text_in_every_locale(seed):
    by_activity = {}
    for t in seed["activityTexts"]:
        by_activity.setdefault(t["activityId"], set()).add(t["locale"])
    for activity in seed["activities"]:
        locales = by_activity.get(activity["id"], set())
        assert set(LOCALES) <= locales, (
            f"activity {activity['id']} missing locales: {set(LOCALES) - locales}"
        )


def test_every_period_has_text_in_every_locale(seed):
    by_period = {}
    for t in seed["sensitivePeriodTexts"]:
        by_period.setdefault(t["periodId"], set()).add(t["locale"])
    for period in seed["sensitivePeriods"]:
        locales = by_period.get(period["id"], set())
        assert set(LOCALES) <= locales, (
            f"period {period['id']} missing locales: {set(LOCALES) - locales}"
        )


def test_activity_ids_are_unique(seed):
    ids = [a["id"] for a in seed["activities"]]
    assert len(ids) == len(set(ids))


def test_age_ranges_are_sane(seed):
    for a in seed["activities"]:
        assert a["ageMinMonths"] <= a["ageMaxMonths"], f"activity {a['id']} has inverted ages"
        assert 0 <= a["ageMinMonths"] <= 72, f"activity {a['id']} min age out of 0-6y range"
        assert 0 <= a["ageMaxMonths"] <= 72, f"activity {a['id']} max age out of 0-6y range"


def test_every_activity_declares_provenance(seed):
    """PRD v0.5 §6 — content must say where it came from."""
    for a in seed["activities"]:
        assert a["provenance"] in ("own_words", "montessori_pd"), (
            f"activity {a['id']} has invalid provenance {a['provenance']!r}"
        )


def test_all_eight_curriculum_areas_have_content(seed):
    """A taxonomy with empty areas looks broken in the UI."""
    present = {a["area"] for a in seed["activities"]}
    assert present == set(AREA_TO_PERIODS), (
        f"areas with no activities: {set(AREA_TO_PERIODS) - present}"
    )


def test_russian_text_is_actually_russian(seed):
    """Guards against an untranslated English string being left in the ru rows."""
    def has_cyrillic(text: str) -> bool:
        return any("Ѐ" <= ch <= "ӿ" for ch in text)

    for t in seed["activityTexts"]:
        if t["locale"] != "ru":
            continue
        for field in ("title", "summary", "whyItMatters", "howToPresent"):
            assert has_cyrillic(t[field]), (
                f"activity {t['activityId']} ru.{field} contains no Cyrillic — untranslated?"
            )


def test_no_stray_foreign_script_characters(seed):
    """Catches accidental CJK/other characters slipping into EN or RU text."""
    def is_stray(ch: str) -> bool:
        cp = ord(ch)
        return (
            0x4E00 <= cp <= 0x9FFF      # CJK
            or 0x3040 <= cp <= 0x30FF   # kana
            or 0xAC00 <= cp <= 0xD7AF   # hangul
            or 0x0590 <= cp <= 0x06FF   # hebrew/arabic
        )

    for rows in (seed["activityTexts"], seed["sensitivePeriodTexts"]):
        for row in rows:
            for key, value in row.items():
                if isinstance(value, str):
                    strays = [ch for ch in value if is_stray(ch)]
                    assert not strays, f"stray characters {strays} in {key}: {value[:60]!r}"
