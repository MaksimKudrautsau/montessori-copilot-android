# Montessori Copilot — PRD v0.3
## "From filing cabinet to daily companion"

Status: **draft for review.** Written to be argued with — see
[§11 Decisions I need from you](#11-decisions-i-need-from-you).

Supersedes: brainstorm v0.1 (AI/cloud concept, abandoned), v0.2 (current
shipped architecture, still valid).

---

## 1. Where we are

v0.2 ships and runs: 24 activities, 9 sensitive periods, per-child journal,
shelf tracker, rule-based recommendations, fully offline, no AI, $0 running
cost. The architecture is sound and the performance is good.

The problem is that it is not yet a product anyone would open twice.

---

## 2. The real problem (diagnosis before solutions)

You called it "limited and boring." Agreed — but the visual layer is the
*smallest* part of why. Four deeper causes, roughly in order of impact:

**2.1 Nothing changes between opens.** Open it Tuesday, it shows exactly what
it showed Monday. There is no reason to return. A reference book you've already
read is not a habit.

**2.2 It takes more than it gives.** The journal is a write-only hole: a parent
types an observation and receives nothing back. Same with the shelf. The app
asks for work and returns storage. That's an unfair trade and users quit it.

**2.3 It's text-only, about physical objects.** "Knobbed cylinder block" means
nothing to a parent who hasn't seen one. Every activity describes a physical
material or a physical interaction, and we show zero pictures. This is the
single biggest gap between "information" and "usable guidance."

**2.4 It doesn't help at the moment of need.** Parents don't browse a
curriculum at 7pm. They reach for a phone because the child is *throwing food,
biting, refusing to sleep, melting down in a shop.* That's the moment with real
urgency and real search volume — and the app has no answer for it.

And underneath all four: **24 activities is roughly two days of browsing before
the content is exhausted.**

---

## 3. Who this is for

**Primary user:** a parent of a 0–6 year old, applying Montessori at home, with
no formal training. Not a teacher. Typically has ~3 minutes of attention, often
one-handed, often with the child in the room.

**Jobs to be done**, in the parent's own words:

| # | Job | Currently served? |
|---|---|---|
| J1 | "Give me something to do with her today that fits where she's at." | Weakly — a static list |
| J2 | "Something's going wrong. What's the Montessori response?" | **Not at all** |
| J3 | "Show me what this actually looks like." | **Not at all** — no images |
| J4 | "Am I doing this right? Is she progressing?" | No — journal returns nothing |
| J5 | "Help me set up our space." | Barely — a tips field |
| J6 | "What do I buy, and what can I make?" | No |

J2 and J3 are the biggest unserved gaps. J2 is the retention driver.

---

## 4. Design principles

1. **The parent is the user. The child never gets the screen.** (Carried from
   v0.2 — this is a Montessori principle, not just a scope decision.)
2. **Give back more than you take.** Any screen that asks for input must return
   something visible within a few uses.
3. **Respect the parent's attention.** No streaks, no daily-login pressure, no
   guilt mechanics. An app that manufactures compulsion contradicts the
   pedagogy it teaches. This rules out most standard retention tricks — we earn
   returns with usefulness instead.
4. **Show, don't only tell.** Every physical thing gets a picture.
5. **Offline-first stays the default.** Network is an enhancement that refreshes
   content, never a requirement to use the app.
6. **Honest sourcing.** Every content item carries its provenance. (See
   README → Content provenance for why this matters here specifically.)
7. **Nothing about the child leaves the device.** Non-negotiable, and it gets
   harder to hold as we add features — see §7.

---

## 5. Epics

Priority order reflects value-per-effort, not your listed order.

### E1 — Content depth *(highest value, highest effort)*
The library goes from a demo to a product.

- **Volume:** 24 → 150+ activities spanning 0–6 years, evenly covering the age
  bands (currently thin at 0–12mo and 4–6y).
- **Depth per activity** — this matters as much as volume. Each becomes:
  - What it is, and **why it matters developmentally**
  - **How to present it** — Montessori "presentation" is a real, specific
    technique (slow, silent, hands modelled before words). This is the single
    most useful thing an untrained parent is missing.
  - **What to observe** — turns each activity into a journal prompt
  - **Common mistakes** — e.g. correcting the child, adding words, intervening
  - **Homemade alternative + rough cost** — most Montessori materials are
    expensive; the DIY version is often the more valuable answer
  - **Image** (see E2)
- **Sensitive periods:** expand from 9 one-liners into real explanations with
  "what you'll notice" and "how to support it."
- **Structure:** add prerequisite/progression links ("this comes after that"),
  so the app can sequence rather than just filter.

### E2 — Imagery and visual system
- **Every activity gets an image.** Non-negotiable for J3. Sourcing options in
  §6.3.
- **Card redesign:** image-led cards, clearer typographic hierarchy, generous
  spacing. The current palette (terracotta/sage/cream) is good — keep it, use
  it better.
- **Activity detail screen** — currently everything is crammed into a card.
  Needs a real detail view with image, sections, and a "log this" action.
- **The parent's own photos:** let them photograph their actual shelf, their
  child working. This is the emotional core of the app and costs nothing —
  photos stay on device.

### E3 — A reason to open it *(the daily loop)*
- **"Today" becomes actually daily:** one focused suggestion per day, rotating,
  chosen by age + sensitive period + what they haven't tried + what they
  dismissed. The rule engine already does most of this — it just needs a date
  seed and a "one thing" surface.
- **Age transitions:** "Emily turns 14 months this week — here's what's
  changing, and what to add to the shelf." Automatic, meaningful, and timed to
  something the parent already cares about.
- **Weekly rhythm, not daily nagging:** one gentle weekly notification
  (shelf rotation + what's new for her age), not a daily ping. Principle 3.

### E4 — Observation → insight *(fixes the unfair trade)*
- **Capture in 5 seconds:** one-tap tags (concentration, frustration, new
  skill, repetition), optional voice note, optional photo. Typing a paragraph
  is too much friction for a parent mid-day.
- **Give it back:**
  - Monthly timeline of the child's observations
  - Patterns: "you've noted deep concentration 8 times this month — mostly with
    practical life work"
  - **First-times auto-surfaced** — the moments parents actually want kept
  - A printable/shareable **month in review** (this is the thing a parent sends
    to a grandparent, and the closest thing to organic growth this app has)

### E5 — Moment-of-need help *(the retention driver)*
A second way into the content, organised by **problem, not by curriculum**:

> throwing food · biting · hitting · tantrums · won't sleep · won't share ·
> picky eating · won't get dressed · screen demands · separation anxiety ·
> won't clean up

Each entry gives: the Montessori *interpretation* of the behaviour (what the
child is actually working on), a concrete **script** — what to say, bilingual
if you keep that from the original repo — what to do instead, and what to
avoid. Plus links to related activities that redirect the underlying drive.

This is what a parent searches Google for at 7pm. Owning that moment is worth
more than any amount of polish.

### E6 — Prepared environment / space setup
Room-by-room guides with photos: entryway, kitchen, bedroom, bathroom, play
area. Per room: what to put at child height, a checklist, "use what you already
own" vs. "worth buying." Serves J5 and J6, both currently unserved.

### E7 — Motion and polish
Purposeful only:
- Shared-element transition from activity card → detail
- Staggered list entrance (subtle, ~40ms offsets)
- Smooth tab/bottom-nav transitions
- One genuinely celebratory moment: a milestone/first-time being recorded
- **Explicitly not:** decorative loops, bouncing, anything that delays a tap.
  Compose makes it tempting to over-animate; the app should feel calm.

### E8 — Content delivery over the network
See §6 — this is the epic whose premise changed after research.

---

## 6. The internet question — findings

You asked to "pull additional info from an official Montessori resource." I
researched what actually exists. The honest picture:

### 6.1 There is no official Montessori API

Neither **AMI** (Association Montessori Internationale) nor **AMS** (American
Montessori Society) publishes a developer API, feed, or open dataset. There is
nothing to connect to.

Worse, **AMS's terms explicitly prohibit** copying, storing, republishing or
distributing their content, and reserve their trademarks; commercial use
requires prior written permission. Scraping them is off the table — legally and
practically (it would break on any redesign).

### 6.2 But the *primary* sources are public domain — and they're better

Maria Montessori's own writings are out of copyright and freely available,
including commercially:

- **The Montessori Method** — Project Gutenberg #39863
- **Dr. Montessori's Own Handbook** — #29635 (this one is essentially a
  material-by-material presentation guide — directly useful for E1)
- **The Montessori Elementary Material** — #42869

This is a *better* source than a secondary organisation's website: it's the
actual origin of the method, it's citable, and it's unambiguously usable.
Project Gutenberg's own trademark is the only thing to avoid reusing.

It also unlocks a genuinely differentiating feature: **quote Montessori
directly** inside relevant activities. No competitor app does this, and it's
free.

Also worth knowing: **"Montessori" itself is generic in the US** — the patent
office determined in the 1960s that no organisation can claim it exclusively.
Your app name is safe.

### 6.3 Images

- **Wikimedia Commons** has a *Montessori materials* category — freely licensed,
  but **licences vary per file** (CC0 / CC BY / CC BY-SA). CC BY-SA carries
  share-alike obligations worth avoiding. Every file must be checked and
  attributed individually. Viable, with per-image bookkeeping.
- **Your own photographs** — you have a child and presumably some of these
  materials. Authentic, zero licensing risk, and better-looking than stock.
- **Commissioned illustration** — most consistent visually, costs money.
- **Stock photo sites** — paid, and licences usually forbid redistribution in a
  way that matters here. Check carefully.

Recommendation: your own photos where possible, Commons (CC0/CC BY only) to
fill gaps, illustration if budget allows.

### 6.4 Recommended architecture — and a challenge to the premise

**Do not fetch from third-party sites at runtime.** It's fragile, legally
exposed, and breaks the offline guarantee.

Instead, a **content pipeline**:

```
You curate offline (tools/generate_content_seed.py, expanded)
        ↓
Versioned content bundle (JSON + images, semver'd)
        ↓
Static hosting — GitHub Pages / Cloudflare Pages ($0)
        ↓
App checks for a newer bundle, downloads, caches into Room
        ↓
Works fully offline forever after
```

This gives you content updates **without a Play release** for every content
change — the real benefit worth having — while keeping the app offline-first
and the cost at zero.

**But the honest challenge:** you may not need this at all. Shipping content
inside app updates is simpler, has zero moving parts, and for a library that
changes a few times a year is entirely sufficient. The network layer is worth
building only if you expect to update content **faster than you ship releases**.
I'd defer it to last, and I'd want you to argue for it rather than assume it.

---

## 7. Privacy consequences (read before approving)

These changes weaken the current guarantee, which is worth protecting
deliberately:

- Today the app declares **no network permission at all** — the strongest
  possible privacy story for an app holding notes about a child.
- E8 requires `INTERNET`. The rule must be: **content flows in; nothing about
  the child ever flows out.** No analytics, no crash reporting that captures
  content, no telemetry — or if any, explicitly opt-in.
- E2/E4 add **photographs of a child** to local storage. They must stay on
  device, be excluded from any cloud backup by default, and the app should say
  so plainly in-product.
- A public Play release will require a **privacy policy** and a Data Safety
  declaration regardless.

---

## 8. Non-goals

- Any child-facing mode or screen time for the child
- AI at runtime (unchanged from v0.2)
- Social feed, comments, community
- Streaks, points, badges, daily-login pressure (principle 3)
- Developmental assessment, screening, or anything a pediatrician should say
- Ecommerce / affiliate links (at least until the content earns trust)

---

## 9. How we'd know it worked

No analytics initially, so these are targets to test qualitatively — ideally
with 5–10 real parents rather than instrumentation:

- Opens **≥3×/week** after week one (today: likely once)
- **≥1 journal entry/week** sustained past a month
- Parent can name one thing they did differently because of the app
- Still installed after 30 days

---

## 10. Suggested phasing

| Phase | Contents | Why this order |
|---|---|---|
| **P1** | E1 (to ~60 activities, full depth fields), E2, E7 basics | Content depth + images is the floor for everything else. Nothing else matters if the library is thin and wordy. |
| **P2** | E5 (moment-of-need), E3 (daily loop) | The two retention features. E5 first — bigger payoff, and it reuses P1 content. |
| **P3** | E4 (insight), E6 (space guides) | Deepens the relationship once people return. |
| **P4** | E8 (network delivery), if justified | Last, and only if §6.4's challenge is answered. |

---

## 11. Decisions I need from you

These change the shape of the work, and I don't want to guess:

1. **Public Play release, or personal/family use?** This drives everything:
   content licensing rigour, privacy policy, Data Safety, support burden. Your
   earlier "host it on Google Play" implies public — confirm.

2. **Who writes 150 activities?** This is the real bottleneck, not code. Options:
   (a) you write them, (b) derive from Montessori's public-domain works,
   (c) pay a trained Montessori guide to write/review, (d) some mix. Given the
   provenance caveat in the README, (c) is the one that makes this trustworthy
   — even a review-only engagement would change the app's credibility.

3. **Images: your own photos, Wikimedia Commons, or commissioned?** Affects
   look, cost, and per-image legal bookkeeping.

4. **Free, paid, or freemium?** Affects whether the content investment ever
   returns anything, and whether AMS-style licensing questions get sharper.

5. **Do you actually want the network layer** (§6.4), or is bundled content per
   release enough for now?

6. **Bilingual?** The original Gemini repo was English/Russian. Worth deciding
   now — it affects every content field and doubles the writing effort.

---

## Appendix — sources consulted

- AMS Terms & Conditions — content reuse and trademark restrictions:
  https://amshq.org/about-montessori/ams-terms-and-conditions/
- Project Gutenberg permissions — commercial reuse of public-domain texts:
  https://www.gutenberg.org/policy/permission.html
- *The Montessori Method*: https://www.gutenberg.org/ebooks/39863
- *Dr. Montessori's Own Handbook*: https://www.gutenberg.org/files/29635/29635-h/29635-h.htm
- *The Montessori Elementary Material*: https://www.gutenberg.org/files/42869/42869-h/42869-h.htm
- "Montessori" as a public-domain term:
  https://imsmontessori.org/news/montessori-community/montessori-in-the-public-domain/
- Wikimedia Commons, Category:Montessori materials:
  https://commons.wikimedia.org/wiki/Category:Montessori_materials
