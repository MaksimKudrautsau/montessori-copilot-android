#!/usr/bin/env python3
"""Sources activity images — from Wikimedia Commons, or your own photographs.

WHY THIS IS A SCRIPT YOU RUN, NOT SOMETHING CLAUDE DID
------------------------------------------------------
The sandbox Claude works in has no route to commons.wikimedia.org, so it can
neither download files nor read their licence metadata. Your Mac can. This
script does the whole job on your machine, and — importantly — reads the
licence and author straight from the Commons API, so credits are recorded
automatically instead of transcribed by hand. Hand-transcribed attribution is
how attribution goes missing.

USAGE
-----
  # 0. one-off
  pip3 install requests pillow

  # 1. find candidates for an activity (prints licence + author for each)
  python3 tools/fetch_images.py search "montessori pink tower"

  # 2. record your choice in tools/image_sources.json (see that file)

  # 3. download everything listed, filter by licence, resize, record credits
  python3 tools/fetch_images.py fetch

  # 4. rebuild the content library so the app picks up the new images
  python3 tools/generate_content_seed.py

Images land in app/src/main/assets/images/ and credits in
tools/image_credits.json, which generate_content_seed.py merges.

LICENCE POLICY lives in tools/image_licences.py and is unit-tested. Anything
share-alike, non-commercial, no-derivatives or unrecognised is refused — see
PRD v0.5 §6.4. A refusal is not a bug; pick a different image.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

from image_licences import format_credit, is_acceptable, rejection_reason

ROOT = Path(__file__).resolve().parent.parent
SOURCES_PATH = ROOT / "tools/image_sources.json"
CREDITS_PATH = ROOT / "tools/image_credits.json"
IMAGES_DIR = ROOT / "app/src/main/assets/images"

COMMONS_API = "https://commons.wikimedia.org/w/api.php"
USER_AGENT = "MontessoriCopilot/0.3 (personal parenting app; contact via GitHub)"

# Bundled images are shown at most full-screen-width on a phone. Anything
# larger is APK weight for nothing.
MAX_WIDTH = 1080
JPEG_QUALITY = 82


def _require_requests():
    try:
        import requests  # noqa: F401
    except ImportError:
        sys.exit("This script needs `requests`. Run:  pip3 install requests pillow")
    import requests
    return requests


def _api_get(params: dict) -> dict:
    requests = _require_requests()
    params = {**params, "format": "json", "formatversion": "2"}
    response = requests.get(
        COMMONS_API, params=params, headers={"User-Agent": USER_AGENT}, timeout=30,
    )
    response.raise_for_status()
    return response.json()


def _image_info(titles: list[str]) -> dict[str, dict]:
    """Licence, author and URL for each File: title."""
    if not titles:
        return {}
    data = _api_get({
        "action": "query",
        "titles": "|".join(titles),
        "prop": "imageinfo",
        "iiprop": "url|extmetadata|size",
    })
    out: dict[str, dict] = {}
    for page in data.get("query", {}).get("pages", []):
        info = (page.get("imageinfo") or [{}])[0]
        meta = info.get("extmetadata", {})

        def field(key: str) -> str | None:
            entry = meta.get(key)
            return entry.get("value") if isinstance(entry, dict) else None

        out[page.get("title", "")] = {
            "url": info.get("url"),
            "width": info.get("width"),
            "height": info.get("height"),
            "licence": field("License"),
            "licence_short": field("LicenseShortName"),
            "author": field("Artist"),
            "description_url": info.get("descriptionurl"),
        }
    return out


def cmd_search(query: str, limit: int) -> None:
    data = _api_get({
        "action": "query",
        "list": "search",
        "srsearch": query,
        "srnamespace": "6",       # File:
        "srlimit": str(limit),
    })
    titles = [hit["title"] for hit in data.get("query", {}).get("search", [])]
    if not titles:
        print("No results.")
        return

    info = _image_info(titles)
    print(f'\n{len(titles)} results for "{query}":\n')
    for title in titles:
        item = info.get(title, {})
        licence = item.get("licence")
        ok = is_acceptable(licence)
        mark = "USABLE  " if ok else "rejected"
        size = f'{item.get("width")}x{item.get("height")}'
        print(f"  [{mark}] {title}")
        print(f"             {size}  licence={licence!r} ({item.get('licence_short')})")
        if not ok:
            print(f"             -> {rejection_reason(licence)}")
        else:
            print(f"             credit: {format_credit(item.get('author'), item.get('licence_short'))}")
        print()

    print("Copy a USABLE title into tools/image_sources.json, then run: "
          "python3 tools/fetch_images.py fetch\n")


def _safe_asset_name(activity_id: int, source_name: str) -> str:
    ext = Path(source_name).suffix.lower() or ".jpg"
    if ext not in (".jpg", ".jpeg", ".png", ".webp"):
        ext = ".jpg"
    return f"activity_{activity_id:03d}{ext}"


def _resize_in_place(path: Path) -> None:
    try:
        from PIL import Image
    except ImportError:
        print("    (pillow not installed — skipping resize; APK will be larger)")
        return
    with Image.open(path) as image:
        if image.width <= MAX_WIDTH:
            return
        ratio = MAX_WIDTH / image.width
        resized = image.convert("RGB").resize(
            (MAX_WIDTH, int(image.height * ratio)), Image.LANCZOS,
        )
        resized.save(path, quality=JPEG_QUALITY, optimize=True)
        print(f"    resized to {MAX_WIDTH}px wide")


def cmd_fetch() -> None:
    if not SOURCES_PATH.exists():
        sys.exit(f"{SOURCES_PATH} not found — nothing to fetch.")

    sources = json.loads(SOURCES_PATH.read_text(encoding="utf-8"))
    entries = {k: v for k, v in sources.items() if not k.startswith("_")}
    if not entries:
        sys.exit("tools/image_sources.json has no entries yet. Add some (see the "
                 "_README key in that file) and re-run.")

    IMAGES_DIR.mkdir(parents=True, exist_ok=True)
    credits: dict[str, dict] = {}
    failures: list[str] = []

    # --- own photographs: no network, no licence check needed ---------------
    local_entries = {k: v for k, v in entries.items() if v.get("local_photo")}
    for activity_id, entry in local_entries.items():
        source = Path(entry["local_photo"])
        if not source.is_absolute():
            source = ROOT / source
        if not source.exists():
            failures.append(f"activity {activity_id}: local photo not found at {source}")
            continue
        asset_name = _safe_asset_name(int(activity_id), source.name)
        target = IMAGES_DIR / asset_name
        target.write_bytes(source.read_bytes())
        _resize_in_place(target)
        credits[activity_id] = {
            "imageAsset": asset_name,
            "imageCredit": entry.get("credit", "Own photograph"),
            "imageLicence": entry.get("licence", "All rights reserved (app author)"),
        }
        print(f"  activity {activity_id}: copied own photo -> {asset_name}")

    # --- Wikimedia Commons ---------------------------------------------------
    commons_entries = {k: v for k, v in entries.items() if v.get("commons_file")}
    if commons_entries:
        requests = _require_requests()
        titles = [v["commons_file"] for v in commons_entries.values()]
        info = _image_info(titles)

        for activity_id, entry in commons_entries.items():
            title = entry["commons_file"]
            item = info.get(title)
            if not item or not item.get("url"):
                failures.append(f"activity {activity_id}: '{title}' not found on Commons")
                continue

            licence = item.get("licence")
            if not is_acceptable(licence):
                failures.append(
                    f"activity {activity_id}: '{title}' REFUSED — {rejection_reason(licence)}"
                )
                continue

            asset_name = _safe_asset_name(int(activity_id), title)
            target = IMAGES_DIR / asset_name
            response = requests.get(
                item["url"], headers={"User-Agent": USER_AGENT}, timeout=60,
            )
            response.raise_for_status()
            target.write_bytes(response.content)
            _resize_in_place(target)

            credits[activity_id] = {
                "imageAsset": asset_name,
                "imageCredit": format_credit(item.get("author"), item.get("licence_short")),
                "imageLicence": item.get("licence_short") or licence,
                "imageSourceUrl": item.get("description_url"),
            }
            print(f"  activity {activity_id}: {title} -> {asset_name}")

    CREDITS_PATH.write_text(
        json.dumps(credits, indent=2, ensure_ascii=False, sort_keys=True), encoding="utf-8",
    )

    print(f"\nWrote {len(credits)} image credit(s) to {CREDITS_PATH.relative_to(ROOT)}")
    if failures:
        print(f"\n{len(failures)} problem(s):")
        for failure in failures:
            print(f"  - {failure}")
        print("\nRefusals are the policy working. Pick a differently-licensed image.")
    print("\nNow run: python3 tools/generate_content_seed.py")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    search = sub.add_parser("search", help="find candidate images on Commons")
    search.add_argument("query")
    search.add_argument("--limit", type=int, default=8)

    sub.add_parser("fetch", help="download everything in tools/image_sources.json")

    args = parser.parse_args()
    if args.command == "search":
        cmd_search(args.query, args.limit)
    else:
        cmd_fetch()


if __name__ == "__main__":
    main()
