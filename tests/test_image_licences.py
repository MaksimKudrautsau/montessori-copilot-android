"""Tests for the image licence policy.

The download itself can't be tested here (no network in the environment that
wrote it, and it hits a live API). But the licence filter is the part where a
mistake becomes a licensing breach in a public Play release, so it is tested
hard — including the cases where a naive substring check would wrongly accept.
"""
import pytest

from tools.image_licences import format_credit, is_acceptable, rejection_reason


@pytest.mark.parametrize("licence", [
    "cc0",
    "CC0",
    "cc0-1.0",
    "cc-by-2.0",
    "cc-by-3.0",
    "cc-by-4.0",
    "CC-BY-4.0",
    "cc-by",
    "pd-old-100",
    "pd-self",
    "pd-us",
    "public domain",
    "public-domain",
])
def test_accepts_permissive_licences(licence):
    assert is_acceptable(licence), f"{licence} should be usable"


@pytest.mark.parametrize("licence", [
    "cc-by-sa-4.0",      # share-alike — the important one
    "cc-by-sa-3.0",
    "CC-BY-SA-2.0",
    "cc-by-nc-4.0",      # non-commercial
    "cc-by-nc-sa-4.0",
    "cc-by-nd-4.0",      # no-derivatives
    "gfdl",
    "gfdl-1.2",
    "fair use",
    "fairuse",
    "non-free",
])
def test_rejects_restrictive_licences(licence):
    assert not is_acceptable(licence), f"{licence} must NOT be bundled"


@pytest.mark.parametrize("licence", ["", None, "   ", "unknown", "custom-terms", "???"])
def test_rejects_unknown_or_missing(licence):
    """Defaulting to allow on an unrecognised string is how a breach happens."""
    assert not is_acceptable(licence)


def test_share_alike_is_rejected_even_though_it_starts_like_cc_by():
    """The trap: 'cc-by-sa-4.0' starts with 'cc-by'. A prefix check would
    wrongly accept it and take on share-alike obligations."""
    assert is_acceptable("cc-by-4.0")
    assert not is_acceptable("cc-by-sa-4.0")


def test_rejection_reasons_are_specific():
    assert "share-alike" in rejection_reason("cc-by-sa-4.0")
    assert "non-commercial" in rejection_reason("cc-by-nc-4.0")
    assert "no-derivatives" in rejection_reason("cc-by-nd-4.0")
    assert "no licence information" in rejection_reason("")


def test_format_credit_strips_html_from_commons_artist_field():
    # Commons returns HTML in the Artist field; the app renders plain text.
    credit = format_credit('<a href="/wiki/User:Bob" title="x">Bob</a>', "CC BY 4.0")
    assert credit == "Bob (CC BY 4.0, via Wikimedia Commons)"
    assert "<" not in credit


def test_format_credit_collapses_whitespace():
    credit = format_credit("Anna   \n  Smith", "CC0")
    assert credit == "Anna Smith (CC0, via Wikimedia Commons)"


def test_format_credit_handles_missing_author():
    assert format_credit(None, "CC0").startswith("Unknown author")
    assert format_credit("<span></span>", "CC0").startswith("Unknown author")


def test_format_credit_always_names_commons():
    """Attribution must identify the source, not just the person."""
    assert "Wikimedia Commons" in format_credit("Bob", "CC BY 4.0")
