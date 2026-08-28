#!/usr/bin/env python3
"""Generates app/src/main/assets/content_seed.json — the curated,
human-authored Montessori content library. This is the only "content
creation" step in the whole app; there is no AI involved anywhere in this
pipeline, by design (see brainstorm v0.2).

Edit the ACTIVITIES / SENSITIVE_PERIODS lists below to add, correct, or
re-band content, then re-run this script. It intentionally does NOT touch
the database directly — see ContentDatabase.kt / ContentSeed.kt for why
seeding happens via a Room onCreate callback instead of a prebuilt .db file.

Usage:
    python3 tools/generate_content_seed.py
"""
import json
from pathlib import Path

OUTPUT_PATH = Path(__file__).resolve().parent.parent / "app/src/main/assets/content_seed.json"

# Age bounds are in months, inclusive. Categories must match
# logic/category_period_map.py's CATEGORY_TO_PERIODS keys.
ACTIVITIES = [
    # --- 0-3 months -----------------------------------------------------
    {"title": "High-contrast mobile", "category": "sensory", "age_min_months": 0, "age_max_months": 4,
     "description": "A simple black-and-white or high-contrast mobile hung within focal range above where the baby lies, to support early visual development.",
     "materials_needed": "High-contrast mobile (e.g. Munari mobile), stand",
     "prepared_environment_tips": "Hang at roughly 20-30cm from the baby's face, not directly over a crib mattress if unsupervised."},
    {"title": "Topponcino handling", "category": "practical_life", "age_min_months": 0, "age_max_months": 3,
     "description": "Using a small, firm pillow (topponcino) to move and hold a newborn gently and consistently, supporting the baby's sense of security.",
     "materials_needed": "Topponcino (firm, flat baby pillow)",
     "prepared_environment_tips": "Keep it consistently in the same spot near the changing/resting area."},

    # --- 3-6 months -------------------------------------------------------
    {"title": "Ring on a ribbon (reach and grasp)", "category": "fine_motor", "age_min_months": 3, "age_max_months": 7,
     "description": "A wooden ring hung at chest height, within reach, to invite intentional reaching, batting, and grasping.",
     "materials_needed": "Wooden ring, ribbon, low stand or frame",
     "prepared_environment_tips": "Position on a low mat, not a crib, so movement is unrestricted."},
    {"title": "Floor mat with mirror", "category": "gross_motor", "age_min_months": 3, "age_max_months": 9,
     "description": "A firm, flat mat with an unbreakable mirror at floor level, encouraging tummy time, head lifting, and self-observation.",
     "materials_needed": "Firm play mat, acrylic (unbreakable) wall mirror mounted low",
     "prepared_environment_tips": "Mirror mounted securely at the child's eye level while lying or sitting."},

    # --- 6-9 months ---------------------------------------------------
    {"title": "Object permanence box with tray", "category": "sensory", "age_min_months": 6, "age_max_months": 12,
     "description": "A box with a hole where a ball is dropped in and disappears into a tray below, reinforced by retrieving it — supports the emerging understanding that objects continue to exist unseen.",
     "materials_needed": "Object permanence box, small ball",
     "prepared_environment_tips": "Present on a low shelf at the child's own height, not handed over each time."},
    {"title": "Treasure basket", "category": "sensory", "age_min_months": 6, "age_max_months": 14,
     "description": "A low, sturdy basket of everyday natural-material objects (wooden spoon, pinecone, shell, fabric swatch) for open-ended sensory exploration while seated.",
     "materials_needed": "Basket, 6-10 safe natural household objects",
     "prepared_environment_tips": "Rotate objects periodically; supervise closely for choking hazards."},

    # --- 9-12 months --------------------------------------------------
    {"title": "Push/pull walker toy", "category": "gross_motor", "age_min_months": 9, "age_max_months": 16,
     "description": "A sturdy, weighted push toy that supports early cruising and independent walking practice.",
     "materials_needed": "Wooden push/pull walker toy",
     "prepared_environment_tips": "Clear a straight, open path along low furniture the child can cruise along."},
    {"title": "Simple shape sorter", "category": "fine_motor", "age_min_months": 10, "age_max_months": 18,
     "description": "A wooden box or cube with one or two shape cut-outs, refining hand-eye coordination and an early sense of order.",
     "materials_needed": "1-2 hole wooden shape sorter",
     "prepared_environment_tips": "Start with a single shape; add more shapes only once one is mastered."},

    # --- 12-18 months -------------------------------------------------
    {"title": "Pouring water, jug to jug", "category": "practical_life", "age_min_months": 14, "age_max_months": 30,
     "description": "Pouring a small amount of water between two small pitchers, building concentration, coordination, and independence.",
     "materials_needed": "Two small stainless-steel or glass pitchers, tray, sponge",
     "prepared_environment_tips": "Set up on a low tray at a table the child can reach; a small towel nearby for spills."},
    {"title": "3-piece ring stacker", "category": "fine_motor", "age_min_months": 10, "age_max_months": 18,
     "description": "Stacking a small number of graduated rings onto a post, refining grasp and an early sense of size sequencing.",
     "materials_needed": "Wooden ring stacker (3 rings)",
     "prepared_environment_tips": "Keep on the active low shelf at consistent placement."},
    {"title": "Naming real objects", "category": "language", "age_min_months": 10, "age_max_months": 24,
     "description": "Naming everyday real objects (cup, spoon, shoe) clearly and consistently during daily routines, rather than relying on picture books alone at this age.",
     "materials_needed": "None — everyday household objects",
     "prepared_environment_tips": "Use the real word, not a diminutive substitute, so vocabulary is accurate from the start."},

    # --- 18-24 months ---------------------------------------------------
    {"title": "Spooning dry beans between bowls", "category": "practical_life", "age_min_months": 18, "age_max_months": 36,
     "description": "Transferring dried beans or chickpeas from one small bowl to another with a spoon, refining wrist control and concentration.",
     "materials_needed": "Two small bowls, spoon, dried beans, tray",
     "prepared_environment_tips": "Use a tray to contain spills; start with larger beans before smaller ones."},
    {"title": "Wiping up a spill", "category": "practical_life", "age_min_months": 18, "age_max_months": 48,
     "description": "A small sponge and bowl kept accessible so the child can independently wipe up their own spills as part of daily practical life.",
     "materials_needed": "Small sponge, small bowl or basin",
     "prepared_environment_tips": "Keep the sponge station at the child's height near the eating/water area."},
    {"title": "Sorting by color", "category": "sensory", "age_min_months": 18, "age_max_months": 36,
     "description": "Sorting a small set of objects into groups by a single quality (color), an early exercise in visual discrimination and classification.",
     "materials_needed": "Small bowls, objects/pom-poms in 2-3 distinct colors",
     "prepared_environment_tips": "Start with just two colors, add a third only once that's easy."},

    # --- 2-3 years --------------------------------------------------------
    {"title": "Dressing frame: large buttons", "category": "practical_life", "age_min_months": 24, "age_max_months": 48,
     "description": "A frame with two fabric panels joined by large buttons, isolating and practicing the fastening motion used in dressing.",
     "materials_needed": "Button dressing frame",
     "prepared_environment_tips": "Introduce one dressing frame type at a time (buttons, then zippers, then snaps)."},
    {"title": "Knobbed cylinder blocks", "category": "sensory", "age_min_months": 30, "age_max_months": 60,
     "description": "Classic Montessori material: cylinders of varying diameter and/or height, removed and replaced into matching sockets, refining visual discrimination of dimension.",
     "materials_needed": "Knobbed cylinder block (one of the four variations)",
     "prepared_environment_tips": "Introduce one block at a time; work on a mat so pieces don't roll away."},
    {"title": "Pink tower", "category": "sensory", "age_min_months": 30, "age_max_months": 60,
     "description": "Ten pink wooden cubes of graduated size, stacked largest to smallest, refining visual-spatial discrimination of size in three dimensions.",
     "materials_needed": "Pink tower (10 cubes)",
     "prepared_environment_tips": "Needs floor space to build; carry cubes with two hands, one at a time."},
    {"title": "Sandpaper letters", "category": "language", "age_min_months": 36, "age_max_months": 54,
     "description": "Letters cut from sandpaper mounted on boards, traced with two fingers while saying the letter's sound, connecting the visual, tactile, and phonetic.",
     "materials_needed": "Sandpaper letters set (lowercase, phonetic sounds)",
     "prepared_environment_tips": "Introduce sounds, not letter names, in small groups of three."},

    # --- 3-4 years ----------------------------------------------------
    {"title": "Care of a plant", "category": "practical_life", "age_min_months": 36, "age_max_months": 72,
     "description": "Watering, wiping leaves, and observing a real houseplant kept at the child's height, building responsibility and care for the environment.",
     "materials_needed": "Small watering can, a hardy houseplant at low height",
     "prepared_environment_tips": "Choose a resilient plant so early over/under-watering isn't discouraging."},
    {"title": "Brown stair", "category": "sensory", "age_min_months": 36, "age_max_months": 60,
     "description": "Ten wooden prisms graduated in thickness, arranged from thickest to thinnest, refining discrimination of a single dimension (width).",
     "materials_needed": "Brown stair (10 prisms)",
     "prepared_environment_tips": "Best on a floor mat; often paired with the pink tower once both are known."},
    {"title": "Moveable alphabet, simple words", "category": "language", "age_min_months": 42, "age_max_months": 66,
     "description": "Building simple phonetic words with cut-out letters before handwriting is fluent, letting composition outpace the physical skill of writing.",
     "materials_needed": "Moveable alphabet box, small objects/picture cards for simple 3-letter words",
     "prepared_environment_tips": "Best introduced only after most sandpaper letter sounds are known."},

    # --- 4-6 years ------------------------------------------------------
    {"title": "Number rods", "category": "sensory", "age_min_months": 48, "age_max_months": 72,
     "description": "Ten rods of increasing length in alternating red/blue decimeter segments, connecting a physical quantity to a number name.",
     "materials_needed": "Number rods (1-10)",
     "prepared_environment_tips": "Needs a long, clear floor mat to lay all ten rods out."},
    {"title": "Table washing", "category": "practical_life", "age_min_months": 48, "age_max_months": 72,
     "description": "A full, ordered practical-life sequence — wetting, soaping, scrubbing, rinsing, drying a small table — combining many refined movements in one purposeful task.",
     "materials_needed": "Small basin, apron, brush, soap, sponge, drying cloth, small table",
     "prepared_environment_tips": "Model the full sequence once, slowly, before inviting the child to try it independently."},
    {"title": "Metal insets, tracing and coloring", "category": "fine_motor", "age_min_months": 42, "age_max_months": 66,
     "description": "Tracing geometric metal insets and filling them with colored pencil lines, building the pencil control needed for handwriting.",
     "materials_needed": "Metal insets set, colored pencils, plain paper",
     "prepared_environment_tips": "Encourage consistent line direction over speed or coverage."},
]

SENSITIVE_PERIODS = [
    {"period_name": "Movement", "age_min_months": 0, "age_max_months": 48,
     "description": "An intense drive to refine gross motor control — reaching, crawling, walking, climbing, and later fine coordination."},
    {"period_name": "Language", "age_min_months": 0, "age_max_months": 72,
     "description": "Rapid, largely effortless absorption of spoken (and later written) language; peaks in the first three years."},
    {"period_name": "Order", "age_min_months": 12, "age_max_months": 48,
     "description": "A strong preference for consistency and predictability in routines, sequences, and the placement of objects in the environment."},
    {"period_name": "Small Objects", "age_min_months": 12, "age_max_months": 30,
     "description": "Fascination with tiny details and small items, refining the pincer grasp and close visual attention."},
    {"period_name": "Refinement of the Senses", "age_min_months": 30, "age_max_months": 72,
     "description": "Heightened interest in comparing and classifying sensory qualities — size, color, texture, sound, weight."},
    {"period_name": "Refinement of Movement", "age_min_months": 18, "age_max_months": 60,
     "description": "Interest in precise, purposeful, sequenced physical actions rather than movement for its own sake."},
    {"period_name": "Coordination of Movement", "age_min_months": 24, "age_max_months": 60,
     "description": "Growing ability to combine multiple refined movements into a single purposeful task."},
    {"period_name": "Independence", "age_min_months": 18, "age_max_months": 60,
     "description": "A drive toward doing things for oneself — dressing, eating, cleaning up — often expressed as 'help me do it myself.'"},
    {"period_name": "Grace and Courtesy", "age_min_months": 30, "age_max_months": 72,
     "description": "Interest in the social forms of a community — greetings, turn-taking, polite requests — through observation and repetition."},
]


def build_seed() -> dict:
    activities = [
        {
            "id": i + 1,
            "title": a["title"],
            "description": a["description"],
            "ageMinMonths": a["age_min_months"],
            "ageMaxMonths": a["age_max_months"],
            "category": a["category"],
            "materialsNeeded": a["materials_needed"],
            "preparedEnvironmentTips": a["prepared_environment_tips"],
        }
        for i, a in enumerate(ACTIVITIES)
    ]
    sensitive_periods = [
        {
            "id": i + 1,
            "periodName": p["period_name"],
            "description": p["description"],
            "ageMinMonths": p["age_min_months"],
            "ageMaxMonths": p["age_max_months"],
        }
        for i, p in enumerate(SENSITIVE_PERIODS)
    ]
    return {"activities": activities, "sensitivePeriods": sensitive_periods}


def main() -> None:
    seed = build_seed()
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT_PATH.write_text(json.dumps(seed, indent=2, ensure_ascii=False), encoding="utf-8")
    print(f"Wrote {len(seed['activities'])} activities and "
          f"{len(seed['sensitivePeriods'])} sensitive periods to {OUTPUT_PATH}")


if __name__ == "__main__":
    main()
