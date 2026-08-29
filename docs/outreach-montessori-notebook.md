# Permission request — The Montessori Notebook

**Purpose:** ask Simone Davies (The Montessori Notebook) for permission to adapt
material from her free activity list, with attribution. See PRD v0.5 §6.2 for
why this is required rather than optional.

**Before sending, edit:** the app name if it changes, and the last line if you'd
rather not offer early access.

**Send to:** the contact address on themontessorinotebook.com. Plain text, no
attachments — attachments from strangers get deleted.

---

## Draft

> **Subject:** Permission request — adapting your activity list for a free
> parenting app
>
> Hello Simone,
>
> I'm a software engineer and the father of a young daughter, and I've been
> building a small Android app to help parents apply Montessori at home. It's
> for parents only — the child never uses it — and it will be free, with no ads
> and no tracking.
>
> I downloaded your *Ultimate list of Montessori activities for babies, toddlers
> and preschoolers*, and it's the clearest thing I've found on the subject,
> particularly for the first year. I'd like to ask permission before using any
> of it.
>
> Specifically, I'd like to adapt some of the activity descriptions into the
> app's content library — rewritten in my own words, not copied — with a visible
> credit to The Montessori Notebook and a link to your books on every screen
> where that material appears. I would not use any of your photographs.
>
> If you'd prefer I didn't, that's completely fine and I'll leave your material
> out entirely. And if you'd rather it worked differently — different wording of
> the credit, a licence arrangement, or a look at the content before it ships —
> I'm glad to work to whatever you're comfortable with.
>
> Happy to send you a copy of the app to try either way.
>
> Thank you for making the list freely available — it's been genuinely useful to
> me as a parent, separate from any of this.
>
> Best regards,
> Max

---

## Notes on the approach

- **Short and specific.** She receives a lot of email. The ask is in one
  paragraph and the answer can be one word.
- **Asks before using, not after.** This is the whole point — asking afterwards
  is an apology, not a request.
- **Names the limits up front** (own words, no photographs), so she doesn't have
  to work out what's being requested.
- **Gives an easy no.** A request that's hard to decline reads as pressure and
  gets ignored.
- **No attachments, no links to click.** Keeps it out of spam.

## If she says no

Nothing is lost. Fall back to PRD §6.3 — Montessori's own public-domain works
are the primary sources, and *Dr. Montessori's Own Handbook* covers the
presentation detail this content most needs. Her PDF stays reference-only:
useful for understanding what good coverage looks like, never a source of
wording.

## If she says yes

Get it **in writing**, keep the email, and record the permission and its terms
in `content_seed.json` provenance fields per activity — not just in someone's
memory. Add the agreed credit to the in-app attributions screen alongside the
image credits (PRD §6.4).
