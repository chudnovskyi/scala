# Task 1 — Validation as a pure function (`val`, methods, `Option`, pattern matching)

Create `src/main/scala/vessel/domain/Validation.scala`.

Write a **pure function** that checks one `VesselPosition` and returns an `Option`:

    object Validation:
      def firstProblem(p: VesselPosition): Option[String]   // None = valid

Rules: vesselId non-blank; latitude in [-90, 90]; longitude in [-180, 180]; speedKnots in [0, 60].
Return the *first* violated rule's message.

Constraints (the point of the exercise):
- No `var`, no `null`, no `return`, no mutable collections, no `throw`.
- Prefer expressions over statements — an `if` / `match` is a value.
- Write tests in `ValidationSpec.scala` first or alongside (valid, each bad field, boundaries, NaN!).
- `isValid(p): Boolean` should be a one-liner derived from `firstProblem`.

Run: `sbt test`, `sbt run`. When done, tell me and I'll review.
