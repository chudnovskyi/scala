# Progress
- [x] Task 1 — validation as `Option[String]`
- [ ] Task 2 — typed errors: `enum`, `Either`, pattern matching  <- you are here

# Task 2 — Errors as data

1. Create `src/main/scala/vessel/domain/ValidationError.scala`:
   an `enum ValidationError` with cases
   `BlankVesselId`, `LatitudeOutOfRange(value: Double)`, `LongitudeOutOfRange(value: Double)`, `SpeedOutOfRange(value: Double)`.
2. In `ValidationError`'s companion/extension or in `Validation`, write
   `def message(e: ValidationError): String` using a `match` (NO `default`/`case _` — let the compiler check exhaustiveness).
3. In `Validation` add
   `def validate(p: VesselPosition): Either[ValidationError, VesselPosition]`
   Same rules and order as Task 1; first error wins; `Right(p)` when valid.
4. Re-implement `isValid` via `validate`. Keep or delete `firstProblem` — if kept, derive it from `validate` + `message`.
5. Update tests: assert on the error *value*, e.g.
   `assertEquals(Validation.validate(ok.copy(latitude = 91.0)), Left(ValidationError.LatitudeOutOfRange(91.0)))`
   Keep the NaN, boundary and "first wins" tests.

Rules: no `var`, no `throw`, no `null`, no `case _` in the `message` match.
Hint: write one tiny helper that turns "condition + error" into an Either, then chain the four checks.
Stretch: `def validateAll(ps: List[VesselPosition]): (List[ValidationError], List[VesselPosition])` using `partition`/`collect`/`foldLeft` — your pick, no loops with mutation.
