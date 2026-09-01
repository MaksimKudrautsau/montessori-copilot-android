"""Licence policy for images bundled in the app.

Separated from the fetch script so it can be unit-tested without network — this
is the part that must not be wrong. Shipping a wrongly-licensed image in a
public Play release is a licensing breach, and "the script downloaded it" is
not a defence.

Policy (PRD v0.5 §6.4):
  ACCEPT  public domain, CC0, CC BY (any version)
  REJECT  CC BY-SA and any other share-alike — the share-alike obligation can
          propagate to the work the image is embedded in, and we are not
          taking that on for a decorative photo
  REJECT  non-commercial (NC) and no-derivatives (ND) — the app is a public
          Play release
  REJECT  anything unrecognised — defaulting to "allow" on an unknown string
          is exactly how a breach happens
"""
from __future__ import annotations

import re

# Matched against Commons' machine-readable `License` field (e.g. "cc0",
# "cc-by-4.0", "pd-old-100"), lower-cased.
_ACCEPT_PATTERNS = (
    r"^cc0(\b|$)",
    r"^cc-by-\d",           # cc-by-2.0, cc-by-4.0 ...
    r"^cc-by$",
    r"^pd(-|$)",            # pd-old, pd-self, pd-us ...
    r"^public[\s-]?domain",
)

# Checked first: if any of these appear, reject regardless of the above.
_REJECT_PATTERNS = (
    r"sa(\b|-|$)",          # share-alike, in any position (cc-by-sa-4.0)
    r"\bnc\b|-nc-|-nc$",     # non-commercial
    r"\bnd\b|-nd-|-nd$",     # no-derivatives
    r"gfdl",                # copyleft, incompatible with our use
    r"fair[\s-]?use",
    r"non[\s-]?free",
)


def normalise(licence: str | None) -> str:
    return (licence or "").strip().lower()


def is_acceptable(licence: str | None) -> bool:
    """True if this licence may be bundled in the app.

    >>> is_acceptable("cc0")
    True
    >>> is_acceptable("CC-BY-4.0")
    True
    >>> is_acceptable("cc-by-sa-4.0")
    False
    >>> is_acceptable("")
    False
    """
    value = normalise(licence)
    if not value:
        return False
    if any(re.search(p, value) for p in _REJECT_PATTERNS):
        return False
    return any(re.search(p, value) for p in _ACCEPT_PATTERNS)


def rejection_reason(licence: str | None) -> str:
    """A human-readable explanation, for the script's output."""
    value = normalise(licence)
    if not value:
        return "no licence information found"
    if re.search(r"sa(\b|-|$)", value):
        return "share-alike (obligations could propagate to the app's content)"
    if re.search(r"\bnc\b|-nc-|-nc$", value):
        return "non-commercial only (this is a public Play release)"
    if re.search(r"\bnd\b|-nd-|-nd$", value):
        return "no-derivatives (images are resized and cropped)"
    if "gfdl" in value:
        return "GFDL copyleft"
    return f"unrecognised licence {licence!r} — not allowing by default"


def format_credit(author: str | None, licence_short: str | None) -> str:
    """The attribution string stored with the image and shown in-app.

    Commons' Artist field often contains HTML; this strips it to plain text
    because the credit is rendered in a Compose Text, not a WebView.

    >>> format_credit('<a href="/wiki/User:Bob">Bob</a>', "CC BY 4.0")
    'Bob (CC BY 4.0, via Wikimedia Commons)'
    """
    name = re.sub(r"<[^>]+>", "", author or "").strip()
    name = re.sub(r"\s+", " ", name) or "Unknown author"
    licence_text = (licence_short or "see Commons").strip()
    return f"{name} ({licence_text}, via Wikimedia Commons)"
