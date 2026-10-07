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
