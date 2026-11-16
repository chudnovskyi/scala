package vessel.processing

import java.time.Instant
import vessel.domain.ValidationError.{BlankVesselId, LatOutOfRange}
import vessel.domain.VesselPosition

class BatchSpec extends munit.FunSuite:

  private val ts = Instant.parse("2026-01-01T00:00:00Z")
  private val ok = VesselPosition("V1", ts, 10.0, 20.0, 5.0)

  test("validateAll on an empty list gives two empty lists") {
    assertEquals(Batch.validateAll(Nil), (Nil, Nil))
  }

  test("validateAll with only valid positions has no errors") {
    val ps = List(ok, ok.copy(vesselId = "V2"))
    assertEquals(Batch.validateAll(ps), (Nil, ps))
  }

  test("validateAll with only invalid positions has no valid ones") {
    val ps = List(ok.copy(vesselId = ""), ok.copy(latitude = 91.0))
    assertEquals(Batch.validateAll(ps), (List(BlankVesselId, LatOutOfRange(91.0)), Nil))
  }

  test("validateAll splits a mixed batch and keeps the original order") {
    val good1 = ok.copy(vesselId = "A")
    val good2 = ok.copy(vesselId = "B")
    val badLat = ok.copy(latitude = 100.0)
    val badId = ok.copy(vesselId = " ")

    val (errors, valid) = Batch.validateAll(List(good1, badLat, good2, badId))

    assertEquals(errors, List(LatOutOfRange(100.0), BlankVesselId))
    assertEquals(valid, List(good1, good2))
  }

  test("validateAll reports one error per invalid position, even when it breaks several rules") {
    val multiBad = ok.copy(latitude = 100.0, speedKnots = -5.0)
    val (errors, valid) = Batch.validateAll(List(multiBad))
    assertEquals(errors, List(LatOutOfRange(100.0)))
    assertEquals(valid, Nil)
  }

  private val t0 = Instant.parse("2026-01-01T00:00:00Z")
  private val t1 = Instant.parse("2026-01-01T01:00:00Z")
  private val t2 = Instant.parse("2026-01-01T02:00:00Z")

  test("groupByVessel on an empty list gives an empty map") {
    assertEquals(Batch.groupByVessel(Nil), Map.empty[String, List[VesselPosition]])
  }

  test("groupByVessel with a single position gives a one-entry map") {
    assertEquals(Batch.groupByVessel(List(ok)), Map("V1" -> List(ok)))
  }

  test("groupByVessel sorts each vessel's positions by timestamp ascending") {
    val late = ok.copy(timestamp = t2)
    val mid = ok.copy(timestamp = t1)
    val early = ok.copy(timestamp = t0)

    assertEquals(Batch.groupByVessel(List(late, early, mid)), Map("V1" -> List(early, mid, late)))
  }

  test("groupByVessel keeps vessels apart and sorts each independently") {
    val a1 = ok.copy(vesselId = "A", timestamp = t1)
    val a0 = ok.copy(vesselId = "A", timestamp = t0)
    val b2 = ok.copy(vesselId = "B", timestamp = t2)
    val b0 = ok.copy(vesselId = "B", timestamp = t0)

    val grouped = Batch.groupByVessel(List(b2, a1, b0, a0))

    assertEquals(grouped.keySet, Set("A", "B"))
    assertEquals(grouped("A"), List(a0, a1))
    assertEquals(grouped("B"), List(b0, b2))
  }
