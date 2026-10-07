# Progress
- [x] Task 1 — validation as `Option[String]`
- [x] Task 2 — typed errors: `enum`, `Either`, pattern matching
- [ ] Task 3 — collections + for-comprehensions  <- you are here

# Task 3 — Processing batches

New file `src/main/scala/vessel/processing/Batch.scala`, `package vessel.processing`, `object Batch:`
Tests in `src/test/scala/vessel/processing/BatchSpec.scala`. Do the parts in order; each gets tests.

A. `def validateAll(ps: List[VesselPosition]): (List[ValidationError], List[VesselPosition])`
   Split a batch into errors and valid positions. Hint: look at `partitionMap` on List.
B. `def groupByVessel(ps: List[VesselPosition]): Map[String, List[VesselPosition]]`
   Each vessel's list sorted by timestamp ascending. Hint: `groupBy`, then transform the Map's values (`view.mapValues`/`map`), `sortBy`.
C. `def averageSpeed(ps: List[VesselPosition]): Option[Double]`
   `None` for an empty list (no division by zero / NaN). Use `foldLeft` OR `sum` — then do it the other way too and compare.
D. `def parse(line: String): Either[String, VesselPosition]`
   Line format: `vesselId,2026-01-01T00:00:00Z,51.9,4.4,12.5`.
   Use a for-comprehension. Tools: `line.split(",")`, `toDoubleOption`, `Option.toRight("msg")`, `Try(Instant.parse(..)).toEither` or `.toOption`.
   Bad field count / bad number / bad timestamp -> `Left("readable message")`.
E. `def process(lines: List[String]): Map[String, Double]` — average speed per vessel from raw lines:
   parse -> validate -> group -> average. Lines that fail parse or validation are dropped.
   (Use `flatMap`/`collect`/for; no `var`, no loops with mutation.)

Rules: no `var`, no `null`, no `throw`, no mutable collections, no `.get` on Option/Either.
