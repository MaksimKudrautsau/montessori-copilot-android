# Montessori Copilot — PRD v0.5
## "From filing cabinet to daily companion"

Supersedes v0.4. This revision locks the eight open decisions and adds three
new sections: content sourcing (§6), monetisation (§9), and bilingual support
(§10). **Two of the answers are recorded with objections** — see §6.2 and §9.

---

## 0. Locked decisions

| # | Decision | Status |
|---|---|---|
| 1 | **Parent-facing.** The child never gets the screen. | ✅ Locked — now a marketing position, not just scope |
| 2 | **Public Google Play release.** | ✅ Locked |
| 3 | Content from The Montessori Notebook free resources | ⛔ **Blocked — see §6.2** |
| 4 | **Wikimedia Commons images**, CC0/CC BY only | ✅ Locked with conditions (§6.4) |
| 5 | **Free, no ads.** No monetisation at launch. | ✅ Locked — revisit at 1,000+ installs with retention data |
| 6 | Network layer | 🔄 **Revised rationale — see §8** |
| 7 | **English + Russian** | ✅ Locked (§10) |
| 8 | **Ages 0–6** | ✅ Locked — 0–18m is a real differentiator |

---

## 1. Where we are

v0.2 ships and runs: 24 activities, 9 sensitive periods, journal, shelf tracker,
rule-based recommendations, fully offline, no AI, $0 running cost. Its feature
set is currently a strict *subset* of a competitor already on Play (§5.2).

## 2. Why it feels limited

**2.1 Nothing changes between opens.** Tuesday looks like Monday.
**2.2 It takes more than it gives.** The journal is a write-only hole.
**2.3 Text-only, about physical objects.** No images, for an app about materials.
**2.4 No help at the moment of need.** Parents reach for a phone when the child
is *throwing food, biting, refusing to sleep* — not to browse a curriculum.

Underneath: **24 activities is two days of browsing.**

## 3. User and jobs

Parent of a 0–6 year old applying Montessori at home, no formal training,
~3 minutes of attention, often one-handed.

| # | Job | Served today? |
|---|---|---|
| J1 | "Something to do with her today that fits her age." | Weakly |
| J2 | "Something's going wrong — what's the Montessori response?" | **No** |
| J3 | "Show me what this actually looks like." | **No** |
| J4 | "Am I doing this right? Is she progressing?" | No |
| J5 | "Help me set up our space." | Barely |
| J6 | "What do I buy, what can I make?" | No |

## 4. Design principles

1. **The parent is the user. The child never gets the screen.**
2. **Give back more than you take.**
3. **Respect the parent's attention.** No streaks, no login pressure, no guilt.
4. **Show, don't only tell.**
5. **Offline-first.** Network enhances; it is never required.
6. **Honest sourcing.** Every content item carries provenance.
7. **Nothing about the child leaves the device.**

---

## 5. Competitive position

### 5.1 Child-facing competitor
A child-taps-the-screen preschool app. Adopting that model means abandoning
principle 1 and rewriting the product. It also sits against mainstream guidance:
the **Montessori Foundation** recommends minimal or no screens under six (under
3, avoid entirely bar video calls), and the **AAP** advises no screens before 18
months and ~1 hour of quality content for ages 2–5. Notably, that app's own
listing sells **"no ads or distracting interruptions"** as a feature — see §9.

### 5.2 Parent-facing competitor *(the real one)*
Ages 18m–5y, curated activities across the classic curriculum areas, age
guidance, short calm sessions, materials you likely own, saved favourites,
private on-device notes, adult-designed with a disclaimer. **This is essentially
our product, already shipped.**

### 5.3 Where we win

| # | Edge | Why it holds |
|---|---|---|
| 1 | **Moment-of-need behaviour guidance (E5)** | Neither competitor has it. Highest urgency. |
| 2 | **Observation → insight (E4)** | They have notes that return nothing. Compounds with use. |
| 3 | **Shelf & rotation tracking** | Already built. We under-sell it. |
| 4 | **Sensitive-period linking** | Already built. Answers *why now*. |
| 5 | **Birth–18 months** | Competitor starts at 18m. Habits form earlier. |
| 6 | **Montessori's own words** | Public domain (§6.3). Free credibility. |
| 7 | **Russian language** | Competitors are English-only (§10). |

### 5.4 Positioning

> For parents raising a child the Montessori way at home, who want real guidance
> rather than another screen for their child — a calm, private companion that
> tells you what to try today, what to do when things go wrong, and what you're
> seeing as your child grows. **The child never touches it.**

Use "Montessori-**inspired**" until content is educator-reviewed, and carry a
disclaimer: not certification, not medical or therapeutic, always supervise.

---

## 6. Content sourcing

### 6.1 The bottleneck
150+ activities × 2 languages × 8 curriculum areas × 0–6 years. This is the
project's real constraint. Code is the easy part.

### 6.2 ⛔ The Montessori Notebook material cannot be used

The PDF is *The ultimate list of Montessori activities for babies, toddlers and
preschoolers* by **The Montessori Notebook** — Simone Davies, author of the
bestselling *The Montessori Toddler*. It's excellent, and it is **not usable in
this app**:

- **"Free to download" is not "free to republish."** It's a lead magnet for a
  commercial author's books and courses — the opposite of a grant of reuse.
- **Her free-resources page states no licence at all.** Under copyright law the
  default is *all rights reserved*. Absence of a licence is not permission; it
  is the strongest form of refusal.
- **The photographs are hers**, taken in her own home and school. Categorically
  not reusable.
- **Ad-supported distribution is commercial use**, which makes this worse, not
  better (§9).

**What we *can* take — the copyright line:**

Copyright protects *expression*, not *facts or ideas*. The Munari mobile, Gobbi
mobile, treasure basket, grasping beads, interlocking discs — these are
established parts of the method, documented in many sources including
Montessori's own public-domain writing. **We may describe the same activities in
our own words. We may not reuse her sentences, her phrasing, her ordering, or
her photographs.**

Two things from it that *are* fair to learn from, because format and taxonomy
aren't protected:

1. **Her record structure** — age / activity name / description / area of
   development / image — validates the schema in §7 E1.
2. **Her infant-oriented development areas** — *visual development, auditory
   materials, grasping materials* — are more useful for 0–12 months than the
   classic eight curriculum areas, which assume a mobile child. This directly
   supports edge #5. Worth adopting as a *secondary* axis for the infant band.

**Recommended action: email her and ask.** One message, explaining a free
parent-facing app, offering attribution and a link to her books. She may say
no, or offer terms — but a licensed collaboration with a recognised Montessori
author would solve the credibility problem in one move. Until there's written
permission, treat this PDF as reference-for-understanding only, and do not let
its wording near `content_seed.json`.

### 6.3 ✅ Primary sources — public domain, use freely

Maria Montessori's own writing is out of copyright and usable commercially:

- **The Montessori Method** — Project Gutenberg #39863
- **Dr. Montessori's Own Handbook** — #29635 — essentially a
  material-by-material presentation guide; the single best free input for the
  "how to present it" field
- **The Montessori Elementary Material** — #42869

Also: **"Montessori" is generic in the US** (patent office, 1960s), so the app
name is safe. AMI/AMS publish no API, and **AMS's terms prohibit** reusing their
content commercially — so neither is a source.

### 6.4 ✅ Images — Wikimedia Commons, with rules

- **CC0 and CC BY only.** Avoid CC BY-SA — share-alike can propagate obligations
  into the app's own content.
- **Licence and author must be recorded per image**, and an in-app attributions
  screen is required. Build this into the content pipeline from day one;
  retrofitting attribution across 150 images is miserable.
- Commons coverage will be uneven. Gaps get filled by **your own photographs** —
  authentic, zero risk, and better-looking than stock.
- Never hotlink; bundle or self-host (§8).

### 6.5 Who writes the activities

Still open, and still the highest-risk item. Options: you write from the
public-domain works (slow, free, legally clean); commission a trained Montessori
guide (costs money, solves credibility); or seek permission from an established
author (§6.2). Given a public release with ads, "AI-written and unreviewed"
is not a defensible position for parenting content.

---

## 7. Epics

### E1 — Content depth
**Fix the taxonomy first.** Four of the eight classic curriculum areas are
missing entirely:

| Current | Target |
|---|---|
| `practical_life` | **Practical Life** ✓ |
| `sensory` | **Sensorial** *(correct Montessori term)* |
| `language` | **Language** ✓ |
| `fine_motor` + `gross_motor` | **Movement** *(merge)* |
| — | **Mathematics** ← missing |
| — | **Art & Music** ← missing |
| — | **Grace & Courtesy** ← missing |
| — | **Culture & Nature** ← missing |

Plus a secondary infant axis for 0–12m (visual / auditory / grasping — §6.2).

**Volume:** 24 → 150+, evenly across 0–6.

**Per-activity fields:** what it is · why it matters developmentally · **how to
present it** · what to observe · common mistakes · homemade alternative + rough
cost · **session length** · **mess level** · supervision note · image +
attribution · **provenance** · EN/RU text.

**Sensitive periods:** expand to real explanations with "what you'll notice."
**Progression links:** prerequisites, so the app can sequence rather than filter.

### E2 — Imagery and visual system
Image on every activity; image-led card redesign; a real activity **detail
screen**; the parent's own photos of their shelf and child (device-only).

### E3 — Daily loop
One rotating focus suggestion per day, seeded by date + age + sensitive period +
not-tried + not-dismissed. Age-transition moments ("Emily turns 14 months this
week"). Weekly rhythm, not daily nagging.

### E4 — Observation → insight
Five-second capture (one-tap tags, voice, photo). Give back: monthly timeline,
patterns, **first-times auto-surfaced**, printable month-in-review.

### E5 — Moment-of-need help *(sharpest edge)*
Browse by problem, not curriculum: throwing food · biting · hitting · tantrums ·
won't sleep · won't share · picky eating · won't dress · screen demands ·
separation anxiety. Each: the Montessori interpretation, a concrete **script in
both languages**, what to avoid, links to activities that redirect the drive.

### E6 — Prepared environment
Room-by-room setup guides with photos and checklists.

### E7 — Motion and polish
Shared-element card→detail, subtle staggered lists, smooth tab transitions, one
celebratory moment on a first-time. No decorative motion; the app should feel calm.

### E8 — Content delivery (§8)

---

## 8. 🔄 Network layer — revised rationale

You said "network for ads, not sure what else." If ads go (§9), there are still
**three genuine reasons**, and they're better ones:

1. **APK size.** 150 activities × 2 languages × images is plausibly 40–80MB of
   assets. Bundling everything bloats the download and hurts install rates.
   Android's **Play Asset Delivery** or an on-demand content bundle solves this
   properly.
2. **Language packs on demand.** An English-speaking user shouldn't download the
   Russian content and vice versa. This alone roughly halves install size.
3. **Content updates without a release.** Fix a typo or add ten activities
   without a Play review cycle.

Architecture — unchanged and still correct:

```
Curate offline (tools/generate_content_seed.py, expanded)
   ↓ versioned bundle (JSON + images, semver'd, per-locale)
   ↓ static hosting — GitHub Pages / Cloudflare Pages ($0)
   ↓ app fetches newer bundle, caches into Room
   ↓ works fully offline forever after
```

**Never fetch third-party sites at runtime.** Content flows from *your* bundle
only. Still phase this last: bundled-in-release is fine until the library is
large enough that size or update cadence hurts.

---

## 9. ✅ Monetisation — free, no ads

**Decided: no ads at launch, no monetisation.** The reasoning, kept because it
should be re-read before anyone reverses this:

**9.1 It contradicts the position we just chose.** The child-facing competitor
literally advertises **"No ads or distracting interruptions"** as a selling
point. Our own principle 3 is about respecting the parent's attention. A calm,
Montessori-inspired app that serves interstitials is self-undermining, and
reviewers will say so in the one-star reviews that shape early installs.

**9.2 It breaks the privacy story, which is currently our strongest asset.**
Today the app declares **no network permission at all** — the strongest possible
position for an app holding notes and photos of a child. Ad SDKs collect device
identifiers and send data off-device, which forces: a privacy policy, Data
Safety disclosures, and EU/UK consent flows. You would trade a genuine
differentiator for very little.

**9.3 Policy is manageable but not free.** Since the app is parent-facing, you
declare an **18+ target audience**, and Google Play's **Families Policy doesn't
apply** — ads are permitted. But Play reviews whether a declared audience is
accurate, and an app whose subject is young children will get scrutiny. Getting
this wrong risks removal, not just a warning.

**9.4 The money isn't there.** Ad revenue on a niche parenting app at early
install volumes is realistically a few dollars a month — not enough to fund the
content work, and far less than the positioning is worth.

**Consequences of this decision, now that it's locked:**

- **"No ads, no tracking, works offline" goes in the store listing** — it's a
  feature competitors advertise, and here it's literally true.
- **Keep `INTERNET` out of the manifest** until §8 genuinely forces it. Every
  day without it is a stronger Data Safety declaration.
- **No analytics SDK either.** That's the same trade in a smaller package.
  Success is measured per §13, with real parents.
- If revenue is wanted later: **one-time paid unlock or paid content packs**,
  which fit the positioning rather than fighting it. Revisit at 1,000+ installs
  with real retention data — not before.

---

## 10. Bilingual (English + Russian)

Locked, and a genuine edge — both competitors are English-only, and Russian
Montessori content is comparatively scarce.

**Implications, none of them trivial:**

- **Content cost doubles.** Every field in E1, for every activity, twice.
  Machine translation is not adequate for pedagogical scripts — these are words
  a parent will say to their child.
- **Schema change.** Content tables need a `locale` column (or per-locale rows)
  rather than single text columns. Cheaper to do now, at 24 activities, than at
  150.
- **UI strings** move to `res/values/` + `res/values-ru/`. Should happen
  regardless — strings are currently hardcoded in composables.
- **Two independent language choices.** UI language and *content/script*
  language aren't the same: a Russian-speaking parent raising a bilingual child
  in the US may want the interface in Russian and E5 scripts in both. The
  original Gemini repo did exactly this — always give both scripts together.
- **Russian data-localisation law (152-FZ)** requires personal data of Russian
  citizens to be stored on servers in Russia. **Staying fully on-device
  sidesteps this entirely** — a further argument against ad SDKs and any
  account system.
- Play listing needs Russian metadata to actually reach Russian-speaking users.

---

## 11. Privacy

- Keep **no network permission** as long as possible; it is a marketable asset.
- If E8 lands: **content flows in, nothing about the child flows out.** No
  analytics that capture content.
- Photos of a child stay on device, excluded from cloud backup by default,
  stated plainly in-product.
- Public release requires a privacy policy and Data Safety declaration
  regardless of ads.

## 12. Non-goals

Child-facing mode · AI at runtime · social feed · streaks and badges ·
developmental screening · ecommerce/affiliate until content earns trust.

## 13. Success measures

Tested qualitatively with 5–10 real parents, not instrumentation: opens
≥3×/week after week one · ≥1 journal entry/week past a month · parent can name
one thing they did differently · still installed at 30 days.

## 14. Phasing

| Phase | Contents | Rationale |
|---|---|---|
| **P0** | Taxonomy fix + locale schema + i18n extraction | Cheap now, expensive at 150 activities. Do before writing content. |
| **P1** | E1 to ~60 activities with full fields; E2; E7 basics | Content + images is the floor. |
| **P2** | E5, then E3 | The retention features. E5 first. |
| **P3** | E4, E6 | Deepen once people return. |
| **P4** | E8 | When size or update cadence justifies it. |

## 15. Still open

1. **Who writes the activities** (§6.5) — the highest-risk unresolved item.
2. **Educator review** — budget for it, or ship "Montessori-inspired,
   unreviewed" with a visible disclaimer?
3. Permission outreach to The Montessori Notebook — draft ready at
   `docs/outreach-montessori-notebook.md`. Sent? Answered?

---

## Appendix — sources

- AMS Terms & Conditions: https://amshq.org/about-montessori/ams-terms-and-conditions/
- Project Gutenberg permissions: https://www.gutenberg.org/policy/permission.html
- *The Montessori Method*: https://www.gutenberg.org/ebooks/39863
- *Dr. Montessori's Own Handbook*: https://www.gutenberg.org/files/29635/29635-h/29635-h.htm
- *The Montessori Elementary Material*: https://www.gutenberg.org/files/42869/42869-h/42869-h.htm
- "Montessori" as public-domain term: https://imsmontessori.org/news/montessori-community/montessori-in-the-public-domain/
- Wikimedia Commons, Montessori materials: https://commons.wikimedia.org/wiki/Category:Montessori_materials
- Montessori Foundation on screens: https://www.montessori.org/screen-time-and-young-minds-ecommendations-for-technology-use-at-home/
- AAP screen-time guidance (summary): https://health.choc.org/updated-aap-recommendations-for-screen-time/
- Play target audience & Families Policy: https://support.google.com/googleplay/android-developer/answer/9867159
- The Montessori Notebook free resources: https://themontessorinotebook.com/free-resources
