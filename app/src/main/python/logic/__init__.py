"""Pure-Python business logic for Montessori Copilot.

Every module in this package is deliberately dependency-free (stdlib only)
and I/O-free: functions take plain dicts/lists in and return plain
dicts/lists out. That means:

  1. They can be unit-tested with plain `pytest`, on a desktop, with no
     Android toolchain at all (see /tests at the project root).
  2. The Kotlin <-> Python boundary (see MontessoriApp's PythonBridge.kt)
     stays a simple JSON-string-in / JSON-string-out contract via `bridge.py`
     — nothing here needs to know Chaquopy or Android exist.

Room (Kotlin) owns all persistence. Nothing in this package touches a
database or the filesystem.
"""
