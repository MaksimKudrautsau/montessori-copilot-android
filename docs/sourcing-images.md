# Sourcing activity images

The app ships with **no photographs**. Every activity shows a tinted tile
carrying its curriculum-area icon instead. That is a deliberate placeholder,
not a bug — it occupies exactly the space a photo will, so adding images causes
no layout change.

This document is how you replace those tiles with real images.

## Why you run this, not Claude

Claude works in a sandbox with no route to `commons.wikimedia.org`, so it can
neither download files nor read their licence metadata. Your Mac can. The
tooling is written; you run it.

There's a second, better reason: the script reads licence and author **straight
from the Commons API** and records them automatically. Hand-transcribed
attribution is how attribution goes missing, and missing attribution on a
CC BY image is a licence breach in a public Play release.

## One-off setup

```bash
pip3 install requests pillow
```

Pillow is optional but recommended — without it images ship at full resolution
and bloat the APK.

## The loop

**1. Find candidates.** `tools/image_sources.json` has a `_suggested_queries`
block with a starting query for each activity worth photographing.

```bash
python3 tools/fetch_images.py search "montessori pink tower"
```

Each result is printed with its licence, marked `USABLE` or `rejected`, and —
for usable ones — the exact credit line that would be stored.

**2. Record your choices** in `tools/image_sources.json`:

```json
{
  "16": { "commons_file": "File:Montessori Pink Tower.jpg" },
  "5":  { "local_photo": "my_photos/permanence_box.jpg", "credit": "Photo by Max" }
}
```

Both kinds of entry work in the same run. Your own photographs are the better
option wherever you can take them: no licensing question at all, and a real
shelf in a real home looks more trustworthy than stock.

**3. Fetch.**

```bash
python3 tools/fetch_images.py fetch
```

Downloads, applies the licence filter, resizes to 1080px wide, writes into
`app/src/main/assets/images/`, and records credits in
`tools/image_credits.json`.

**4. Rebuild the content library and the app.**

```bash
python3 tools/generate_content_seed.py
./gradlew assembleDebug
```

Then **uninstall the app before reinstalling** — Room only seeds the content
database when it is created, so an existing install keeps the old content.

## The licence policy

Enforced in `tools/image_licences.py`, unit-tested in
`tests/test_image_licences.py`.

| | |
|---|---|
| **Accepted** | Public domain, CC0, CC BY (any version) |
| **Refused** | CC BY-**SA** and anything share-alike |
| **Refused** | Non-commercial (NC), no-derivatives (ND) |
| **Refused** | GFDL, fair use, non-free |
| **Refused** | Anything unrecognised |

Two of those deserve explaining:

**Share-alike is refused** even though it's free, because the obligation can
propagate to the work the image is embedded in. That is not a trade worth
making for a decorative photo.

**Unrecognised licences are refused** rather than allowed. Defaulting to
"allow" on a string nobody anticipated is precisely how a breach happens.

A refusal is the policy working. Pick a different image.

There's also a subtle trap the tests cover: `cc-by-sa-4.0` *starts with*
`cc-by`. A naive prefix check accepts it and silently takes on share-alike
obligations. The filter checks rejections first for exactly this reason.

## What not to photograph

Eleven activities are deliberately left as area tiles — they're techniques and
conversations, not objects: naming real objects, the three-period lesson, sound
games, waiting for a turn, greeting a visitor, the silence game, and similar.
A stock photo of a child would add nothing and would raise its own questions
about photographing children.

## When images exist

- Every image appears in the app's **Sources & credits** screen automatically
  (ⓘ in the child list). An image whose credit wasn't recorded won't appear
  there, which makes the gap visible rather than silent.
- `generate_content_seed.py` prints an **ERROR** if any image lacks a credit.
- Watch the APK size. Forty-five images at 1080px is roughly 10–20MB, which is
  the point at which PRD v0.5 §8's on-demand delivery starts to earn its keep.
