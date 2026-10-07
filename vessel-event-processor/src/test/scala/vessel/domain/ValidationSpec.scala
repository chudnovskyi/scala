package vessel.domain

import java.time.Instant

class ValidationSpec extends munit.FunSuite:

  private val ok = VesselPosition("V1", Instant.parse("2026-01-01T00:00:00Z"), 10.0, 20.0, 5.0)

  test("a valid position passes (placeholder - replace with your own tests)") {
    assertEquals(ok.vesselId, "V1")
  }
