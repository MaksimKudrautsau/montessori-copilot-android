# Montessori Copilot — PRD v0.4
## "From filing cabinet to daily companion"

Status: **draft for review.** Supersedes v0.3 (kept for history). Changes in
this revision are driven by two competitor listings supplied by Max — see
[§6 Competitive landscape](#6-competitive-landscape-and-positioning), which is
the most important new section and changes the differentiation strategy.

---

## 1. Where we are

v0.2 ships and runs: 24 activities, 9 sensitive periods, per-child journal,
shelf tracker, rule-based recommendations, fully offline, no AI, $0 running
cost. Architecture is sound, performance is good.

It is not yet a product anyone would open twice — and, per §6, its current
feature set is a strict *subset* of an app already on the Play Store.

---

## 2. The real problem (diagnosis before solutions)

"Limited and boring" is right, but the visual layer is the *smallest* cause.
Four deeper ones, in order of impact:

**2.1 Nothing changes between opens.** Tuesday looks like Monday. A reference
book you've already read is not a habit.

**2.2 It takes more than it gives.** The journal is a write-only hole. The app
asks for work and returns storage. Users quit that trade.

**2.3 It's text-only, about physical objects.** "Knobbed cylinder block" means
nothing to a parent who's never seen one. Every activity describes a physical
material; we show zero pictures.

**2.4 It doesn't help at the moment of need.** Parents don't browse a curriculum
at 7pm. They reach for a phone because the child is *throwing food, biting,
refusing to sleep, melting down in a shop.*

Underneath all four: **24 activities is about two days of browsing.**

---

## 3. Who this is for

**Primary user:** a parent of a 0–6 year old applying Montessori at home, with
no formal training. ~3 minutes of attention, often one-handed, child in the room.

| # | Job to be done | Served today? |
|---|---|---|
| J1 | "Give me something to do with her today that fits where she's at." | Weakly |
| J2 | "Something's going wrong. What's the Montessori response?" | **No** |
| J3 | "Show me what this actually looks like." | **No** — no images |
| J4 | "Am I doing this right? Is she progressing?" | No |
| J5 | "Help me set up our space." | Barely |
| J6 | "What do I buy, and what can I make?" | No |

J2 and J3 are the biggest gaps. J2 is the retention driver **and**, per §6, our
clearest differentiator.

---

## 4. Design principles

1. **The parent is the user. The child never gets the screen.** Now also a
   *competitive* position — see §6.1.
2. **Give back more than you take.**
3. **Respect the parent's attention.** No streaks, no daily-login pressure, no
   guilt mechanics. An app that manufactures compulsion contradicts the
   pedagogy it teaches.
4. **Show, don't only tell.**
5. **Offline-first stays the default.** Network is enhancement, never
   requirement.
6. **Honest sourcing.** Every content item carries provenance.
7. **Nothing about the child leaves the device.**

---

## 5. Epics

### E1 — Content depth *(highest value, highest effort)*

**5.1.1 Fix the taxonomy first — this is a credibility problem.**

Our current five categories aren't the Montessori curriculum areas. Both
competitor apps use the real ones, and anyone who knows the method will notice
immediately. Proposed:

| Current | Proposed | Note |
|---|---|---|
| `practical_life` | **Practical Life** | correct already |
| `sensory` | **Sensorial** | "Sensorial" is the actual Montessori term |
| `language` | **Language** | correct already |
| `fine_motor` + `gross_motor` | **Movement** | merge; Montessori treats movement as one area |
| — | **Mathematics** | **entirely missing** |
| — | **Art & Music** | **entirely missing** |
| — | **Grace & Courtesy** | **entirely missing** — social skills, a core area |
| — | **Culture & Nature** | **missing** — seasons, animals, time, geography |

Four of eight areas are absent. This is the biggest single content gap, bigger
than raw volume. Implementation note: this is a schema change touching the
category enum, `content_seed.json`, and `category_period_map.py`.

**5.1.2 Volume:** 24 → 150+ activities across 0–6, evenly covering age bands
(currently thin at 0–12mo and 4–6y).

**5.1.3 Depth per activity** — matters as much as volume:

- What it is, and **why it matters developmentally**
- **How to present it** — Montessori "presentation" is a specific technique
  (slow, silent, hands modelled before words). The single most useful thing an
  untrained parent is missing.
- **What to observe** — turns each activity into a journal prompt
- **Common mistakes** — correcting, narrating, intervening
- **Homemade alternative + rough cost**
- **Session length** — "short, calm sessions" *(validated by competitor B)*
- **Mess/cleanup level** — *"low-mess" is a smart parent framing we'd missed
  (competitor B); it's often the deciding factor on a weekday evening*
- **Supervision note** where relevant (small parts, water, scissors)
- **Image** (see E2)

**5.1.4 Sensitive periods:** expand 9 one-liners into real explanations with
"what you'll notice" and "how to support it."

**5.1.5 Progression links:** prerequisite/next-step relationships so the app can
*sequence*, not just filter.

### E2 — Imagery and visual system
- **Every activity gets an image.** Non-negotiable for J3. Sourcing in §7.3.
- **Card redesign:** image-led cards, clearer hierarchy, generous spacing. Keep
  the terracotta/sage/cream palette — use it better.
- **Activity detail screen** — a real detail view, not a crammed card.
- **The parent's own photos** — their actual shelf, their child working. The
  emotional core, costs nothing, stays on device.

### E3 — A reason to open it *(the daily loop)*
- **"Today" becomes actually daily:** one focused suggestion, rotating, seeded
  by date + age + sensitive period + not-yet-tried + not-dismissed. The rule
  engine already does most of this.
- **Age transitions:** "Emily turns 14 months this week — here's what's
  changing, and what to add to the shelf."
- **Weekly rhythm, not daily nagging** (principle 3).

### E4 — Observation → insight *(fixes the unfair trade)*
- **Capture in 5 seconds:** one-tap tags (concentration, frustration, new skill,
  repetition), optional voice note, optional photo.
- **Give it back:** monthly timeline; patterns ("deep concentration noted 8×
  this month, mostly practical life"); **first-times auto-surfaced**; a
  printable **month in review**.

> Competitor B also offers "private notes" — and has the same write-only flaw.
> Turning notes into something the parent receives back is a real, defensible
> edge, not a nice-to-have.

### E5 — Moment-of-need help *(the retention driver and our sharpest edge)*
A second way into the content, organised by **problem, not curriculum**:

> throwing food · biting · hitting · tantrums · won't sleep · won't share ·
> picky eating · won't get dressed · screen demands · separation anxiety ·
> won't clean up

Each gives: the Montessori *interpretation* of the behaviour, a concrete
**script** (bilingual if we keep that from the original repo), what to do
instead, what to avoid, and links to activities that redirect the drive.

Neither competitor does this. It's what parents search for at 7pm.

### E6 — Prepared environment / space setup
Room-by-room guides with photos: entryway, kitchen, bedroom, bathroom, play
area. Per room: what to put at child height, a checklist, "use what you own" vs
"worth buying." Serves J5 and J6.

### E7 — Motion and polish
Purposeful only: shared-element card → detail transition; subtle staggered list
entrance (~40ms); smooth tab transitions; one genuinely celebratory moment when
a first-time is recorded. **Not:** decorative loops, bounce, anything that
delays a tap. Compose makes over-animating easy; the app should feel calm.

### E8 — Content delivery over the network
See §7.4 — the epic whose premise changed after research.

---

## 6. Competitive landscape and positioning

Max supplied two Play Store listings. They point in **opposite directions**, and
the difference between them is the central strategic question in this document.

### 6.1 Competitor A — child-facing preschool app

A **child-facing** app: the child taps the screen. Domains span early math,
shapes/patterns/logic, "practical life inspired" exercises, music, time and
routines, drawing/coloring, seasons and nature, animal sounds, and vocabulary.
Positioning: calm, self-correcting, no ads, no interruptions.

**Adopting this feature list means becoming a different product** and abandoning
design principle #1. Before considering that, two things are worth knowing:

**It sits in direct tension with mainstream Montessori guidance on screens.**
The Montessori Foundation recommends **minimal or no screen time under six** —
under 3, "avoid screens entirely, except the occasional family video call" —
on the grounds that the absorbent mind develops through touching, climbing and
building, not swiping. Pediatric guidance runs parallel: the AAP advises **no
screens before 18 months** and about **one hour of high-quality content daily
for ages 2–5**, with recent emphasis on quality and context over raw limits.

So an app marketed as "hands-on" where the child taps glass is, at minimum, a
marketing claim in tension with the method it invokes — and its whole
addressable use is bounded by an hour a day that most parents are already
spending elsewhere.

**This is an asset for us, not just a critique.** Staying parent-facing lets us
say something no child-facing competitor can: *the child never touches this app;
it exists to get them away from screens and onto real materials.* That is a
sharper, more Montessori-consistent market position than competing on cartoon
production values.

**Two cautions:** their name carries a ™ — don't reuse it or their listing copy.
And their listing is worth reading for the **domain taxonomy** only, which is
what exposed the four missing curriculum areas in §5.1.1.

### 6.2 Competitor B — parent-facing activity app *(the real competitor)*

Parent-facing, ages 18m–5y: curated activities across Practical Life, Sensorial,
Language, Math, Movement, Art & Music, and Grace & Courtesy; age guidance and
"easy setups for short, calm sessions"; materials you likely already have; save
favourites; private on-device notes; explicitly designed for adults, with a
disclaimer that it isn't certification, medical, or therapeutic.

**This is essentially our product, already shipped.** That's worth sitting with
rather than glossing over.

What it validates — several v0.3 bets were right: parent-facing; on-device
private notes; "materials you likely have"; calm, short sessions; the real
curriculum taxonomy.

What it exposes — **our v0.2 feature set is a strict subset of theirs.** Today
we have no differentiation at all. We're behind on content volume, and level on
everything else.

### 6.3 Where we can actually win

Not on "curated Montessori activities" — that ground is taken. Six candidate
edges, ranked by defensibility:

| # | Edge | Why it holds |
|---|---|---|
| 1 | **Moment-of-need behaviour guidance (E5)** | Neither competitor has it. Highest-urgency parent need. |
| 2 | **Observation → insight (E4)** | B has notes but returns nothing. Compounding value: the longer you use it, the better it gets. |
| 3 | **Shelf & rotation tracking** | The prepared environment made operational. B has nothing here; we already ship it. |
| 4 | **Sensitive-period linking** | Answers *why now*, not just *what*. Already built. |
| 5 | **Birth–18 months** | B starts at 18m. Infancy is underserved and it's when parents form habits — get them early and they stay. |
| 6 | **Primary-source Montessori quotes** | Public domain (§7.2). Credibility neither competitor has. Free. |

Note that #3 and #4 already exist in v0.2 — we're under-selling what we have.

### 6.4 Positioning statement (draft)

> For parents raising a child the Montessori way at home, who want real
> guidance rather than another screen for their child — a calm, private
> companion that tells you what to try today, what to do when things go wrong,
> and what you're seeing as your child grows. The child never touches it.

### 6.5 Naming and claims

Competitor B says "Montessori-**inspired**" — a deliberate hedge. Given our own
provenance caveat (README), we should do the same until content is
educator-reviewed. Also adopt a disclaimer in B's style: not certification,
not medical or therapeutic, always supervise. Cheap, honest, and expected in
this category.

---

## 7. Content sourcing and the internet question

You asked to pull from "an official Montessori resource." Research findings:

### 7.1 There is no official Montessori API
Neither **AMI** nor **AMS** publishes an API, feed, or open dataset. Further,
**AMS's terms explicitly prohibit** copying, storing, republishing or
distributing their content, and reserve their trademarks; commercial use needs
prior written permission. Scraping is off the table legally and practically.

### 7.2 But the *primary* sources are public domain — and they're better
Maria Montessori's own writings are out of copyright and free to use
commercially:

- **The Montessori Method** — Project Gutenberg #39863
- **Dr. Montessori's Own Handbook** — #29635 (essentially a
  material-by-material presentation guide — directly feeds §5.1.3)
- **The Montessori Elementary Material** — #42869

Better than a secondary organisation's website: it's the origin of the method,
citable, and unambiguously usable. Only Project Gutenberg's own trademark needs
avoiding. This unlocks edge #6 in §6.3.

Also: **"Montessori" is generic in the US** — the patent office determined in
the 1960s that no organisation can claim it exclusively. The app name is safe.

### 7.3 Images
- **Wikimedia Commons** has a *Montessori materials* category — free, but
  **licences vary per file** (CC0 / CC BY / CC BY-SA). Avoid CC BY-SA
  (share-alike). Each file needs individual checking and attribution.
- **Your own photographs** — authentic, zero licensing risk, better-looking
  than stock, and you have a child and some of these materials.
- **Commissioned illustration** — most visually consistent, costs money.
- **Stock sites** — paid, and licences often forbid the redistribution that
  matters here.

Recommendation: your own photos first, Commons (CC0/CC BY only) to fill gaps.

### 7.4 Recommended architecture — and a challenge to the premise

**Do not fetch from third-party sites at runtime.** Fragile, legally exposed,
breaks the offline guarantee.

```
You curate offline (tools/generate_content_seed.py, expanded)
        ↓  versioned content bundle (JSON + images, semver'd)
        ↓  static hosting — GitHub Pages / Cloudflare Pages ($0)
        ↓  app checks for a newer bundle, downloads, caches into Room
        ↓  works fully offline forever after
```

Content updates without a Play release is the real benefit worth having.

**The honest challenge:** you may not need this at all. Shipping content inside
app updates has zero moving parts and is fine for a library that changes a few
times a year. Build it only if you'll update content *faster than you ship
releases*. Deferred to last; argue for it rather than assuming it.

---

## 8. Privacy consequences

- Today the app declares **no network permission at all** — the strongest
  possible privacy story for an app holding notes about a child. Worth
  protecting; it's also marketable (see §6.4).
- E8 requires `INTERNET`. Rule: **content flows in; nothing about the child
  ever flows out.** No analytics or crash reporting that captures content, or
  strictly opt-in.
- E2/E4 add **photographs of a child** to local storage: stay on device,
  excluded from cloud backup by default, stated plainly in-product.
- Public Play release requires a **privacy policy** and Data Safety declaration.

---

## 9. Non-goals

- Any child-facing mode or screen time for the child *(see §6.1 — this is now a
  positioning decision, not just scope)*
- AI at runtime
- Social feed, comments, community
- Streaks, points, badges, daily-login pressure (principle 3)
- Developmental assessment or screening
- Ecommerce / affiliate links, at least until the content earns trust

---

## 10. How we'd know it worked

No analytics initially — test qualitatively with 5–10 real parents:

- Opens **≥3×/week** after week one
- **≥1 journal entry/week** sustained past a month
- Parent can name one thing they did differently because of the app
- Still installed after 30 days

---

## 11. Suggested phasing

| Phase | Contents | Why this order |
|---|---|---|
| **P1** | E1 taxonomy fix + content to ~60 activities with full depth fields; E2; E7 basics | Content depth + images is the floor. The taxonomy fix is cheap and closes a credibility gap. |
| **P2** | E5, then E3 | The two retention features. E5 first — it's differentiator #1 and reuses P1 content. |
| **P3** | E4, E6 | Deepens the relationship once people return. E4 is differentiator #2. |
| **P4** | E8, if justified | Only if §7.4's challenge is answered. |

---

## 12. Decisions I need from you

1. **Parent-facing or child-facing?** *(new, and now the biggest one)* The two
   listings you sent are opposite products. My recommendation is to stay
   parent-facing and make "the child never touches this app" an explicit selling
   point — it's Montessori-consistent, evidence-backed (§6.1), and avoids
   competing on animation budgets in a crowded category. Going child-facing is a
   legitimate business choice, but it's a rewrite, not an upgrade.

2. **Public Play release, or personal/family use?** Drives licensing rigour,
   privacy policy, Data Safety, support burden.

3. **Who writes 150 activities?** The real bottleneck, not code. (a) you,
   (b) derive from the public-domain works, (c) pay a trained Montessori guide
   to write or review, (d) a mix. Given the provenance caveat, (c) — even
   review-only — is what would make this trustworthy rather than plausible.

4. **Images: your own photos, Commons, or commissioned?**

5. **Free, paid, or freemium?** Competitor B is shipping; worth checking its
   price before deciding.

6. **Do you actually want the network layer** (§7.4), or is bundled content per
   release enough?

7. **Bilingual?** The original Gemini repo was English/Russian. Decide now — it
   affects every content field and doubles the writing effort.

8. **Age range: keep 0–6, or narrow?** Competitor B is 18m–5y. Our 0–18m
   coverage is a genuine differentiator (§6.3 #5) but adds content work.

---

## Appendix — sources consulted

- AMS Terms & Conditions: https://amshq.org/about-montessori/ams-terms-and-conditions/
- Project Gutenberg permissions: https://www.gutenberg.org/policy/permission.html
- *The Montessori Method*: https://www.gutenberg.org/ebooks/39863
- *Dr. Montessori's Own Handbook*: https://www.gutenberg.org/files/29635/29635-h/29635-h.htm
- *The Montessori Elementary Material*: https://www.gutenberg.org/files/42869/42869-h/42869-h.htm
- "Montessori" as a public-domain term: https://imsmontessori.org/news/montessori-community/montessori-in-the-public-domain/
- Wikimedia Commons, Category:Montessori materials: https://commons.wikimedia.org/wiki/Category:Montessori_materials
- Montessori Foundation on screen time: https://www.montessori.org/screen-time-and-young-minds-ecommendations-for-technology-use-at-home/
- AAP screen time guidance (summary): https://health.choc.org/updated-aap-recommendations-for-screen-time/
