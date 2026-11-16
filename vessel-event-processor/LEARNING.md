# vessel-event-processor: learning context (resume file)

Paste or point Claude at this file at the start of a new session to continue where we stopped.

## Goal
Learn idiomatic Scala 3 by building a small vessel-event pipeline. Learner comes from Java backend /
distributed systems (Kafka, Docker known: don't teach those). This is a LEARNING project, not production-grade.

## Mentor rules (how Claude should work)
- Act as mentor / pair programmer. Do NOT implement the project for the learner.
- Flow per task: give a small task -> learner implements -> review -> explain what is idiomatic Scala vs
  "Java written in Scala" -> next task.
- Prefer hints over full solutions. If the learner makes a mistake, explain the concept first.
  Give a full solution only when explicitly asked.
- Introduce concepts progressively, not all at once. Every few tasks, give a small challenge that combines
  earlier concepts.
- Learner has asked Claude to write some tests ("create tests for A") and to show syntax of alternatives:
  that is OK when requested explicitly. Otherwise encourage the learner to write tests.
- Dependencies minimal: Scala stdlib + munit only. NO Cats, ZIO, Akka, Spark.
- Kafka comes LATER, only once domain logic is comfortable. Then: Kafka topic -> Scala consumer -> domain
  processing -> Kafka producer -> output topic, with Kafka code kept separate from the domain (own package).
- Do not run git commands for the learner unless asked; they commit themselves.
  (Repo-local git identity was set to the personal account; the global one is the work account.)

## Stack
- Scala 3.10.0, sbt 1.12.15, munit 1.3.6 (Test scope), JDK 21 recommended (IntelliJ project JDK must exist).
- Commands: `sbt compile`, `sbt test`, `sbt "testOnly vessel.processing.BatchSpec"`, `sbt ~test`, `sbt run`.
- Compiler flags: -deprecation -feature -Wunused:all.
- IntelliJ: open `vessel-event-processor/` as its own sbt project (Scala plugin installed).

## Domain
```scala
case class VesselPosition(vesselId: String, timestamp: Instant, latitude: Double, longitude: Double, speedKnots: Double)
```
Eventual pipeline: receive -> validate -> filter invalid -> enrich/transform -> group by vessel ->
stats (average speed) -> detect events (stopped / moving / enter-leave area) -> produce processed events.

## Layout
```
src/main/scala/vessel/
  Main.scala                      @main entry point
  domain/VesselPosition.scala
  domain/ValidationError.scala    enum: BlankVesselId, LatOutOfRange(v), LonOutOfRange(v), SpeedOutOfRange(v)
  domain/ValidationV1.scala       Option[String] version (kept for comparison)
  domain/ValidationV2.scala       Either[ValidationError, VesselPosition] + message(e)
  processing/Batch.scala          validateAll (part A done)
src/test/scala/vessel/
  domain/ValidationV1Spec.scala, ValidationV2Spec.scala
  processing/BatchSpec.scala
TASKS.md                          current task descriptions
```
Validation rules (first violated wins, in this order): vesselId not blank; latitude in [-90,90];
longitude in [-180,180]; speedKnots in [0,60]. NaN must be rejected (ranges are phrased as
"inside range", negated).

## Progress
- [x] Task 1: validation as Option[String] (val, def, object, if-expression, Option, orElse, munit).
- [x] Task 2: typed errors: enum ADT, Either, exhaustive pattern matching (no `case _`).
- [ ] Task 3: collections + for-comprehensions (see TASKS.md for parts A-E)
  - [x] A `Batch.validateAll` = `ps.partitionMap(ValidationV2.validate)`; BatchSpec has 5 tests (all pass).
  - [ ] B `groupByVessel(ps): Map[String, List[VesselPosition]]`, each list sorted by timestamp
  - [ ] C `averageSpeed(ps): Option[Double]` (None when empty)
  - [ ] D `parse(line): Either[String, VesselPosition]` via for-comprehension
        (format `vesselId,2026-01-01T00:00:00Z,51.9,4.4,12.5`; toDoubleOption, toRight, Try(...).toEither)
  - [ ] E `process(lines): Map[String, Double]` average speed per vessel (parse -> validate -> group -> average)

## Concepts covered so far
val vs var, type inference, def/methods, object, case class + copy, if/match as expressions,
Option (Some/None, orElse, exists, isEmpty), Either (Left/Right, isRight, toOption, left projection),
enum/ADT, exhaustive pattern matching, tuples + destructuring, partitionMap, munit
(assertEquals, assert, fail, generated tests via foreach).

## Review notes / feedback already given (learner tendencies)
- Java habits seen: `== null` checks on Double (impossible) and on String (domain shouldn't hold nulls),
  braces `{ }` around bodies (Scala 3 style prefers indentation; learner mixes both), calling the same
  function twice, shadowing names (`ps` for both list and element), wrong `.left` projection usage.
- Good: positive range checks for NaN-safety, one-liner `isValid`, passing methods as functions.
- Open improvements: unify brace style; delete ValidationV1 + rename ValidationV2 -> Validation when done
  comparing; move `message` onto ValidationError companion (or an extension method later);
  remove duplication between ranges and message texts.

## Upcoming roadmap
- Task 3 B-E (above), then a combined CHALLENGE task.
- Task 4: enrichment / transformation, extension methods (e.g. distance, knots conversions), more traits.
- Task 5: event detection: vessel stopped / moving (sealed trait or enum VesselEvent), geofence
  enter/leave with state via foldLeft / sliding windows.
- Task 6: a processing pipeline object composing everything; a second combined challenge.
- Then Kafka + Docker: consumer -> domain -> producer in a separate `kafka` package/module; JSON serde
  (keep deps minimal), integration tests with Docker.

## Open housekeeping
- Commits are made by the learner. Suggested commit messages given so far:
  "Add VesselPosition validation with tests", "Add typed ValidationError and Either-based validation",
  "Add Batch.validateAll to split valid and invalid positions".
