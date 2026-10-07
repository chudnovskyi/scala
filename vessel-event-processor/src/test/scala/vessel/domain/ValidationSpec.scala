package vessel.domain

import java.time.Instant

class ValidationSpec extends munit.FunSuite:

  private val ok = VesselPosition("V1", Instant.parse("2026-01-01T00:00:00Z"), 10.0, 20.0, 5.0)

  test("a valid position has no problem and is valid") {
    assertEquals(Validation.firstProblem(ok), None)
    assert(Validation.isValid(ok))
  }

  test("a blank vesselId is rejected") {
    List("", "   ").foreach { id =>
      val problem = Validation.firstProblem(ok.copy(vesselId = id))
      assert(problem.exists(_.contains("vesselId")), s"id='$id' gave $problem")
    }
  }

  test("latitude above 90 is rejected and mentions lat") {
    val problem = Validation.firstProblem(ok.copy(latitude = 90.1))
    assert(problem.exists(_.contains("lat")), s"unexpected: $problem")
    assert(!Validation.isValid(ok.copy(latitude = 90.1)))
  }

  test("speed outside [0, 60] is rejected with the speed message") {
    List(-0.1, 60.1).foreach { s =>
      val problem = Validation.firstProblem(ok.copy(speedKnots = s))
      assert(problem.exists(_.contains("speed")), s"speed=$s gave $problem")
    }
  }

  test("when several rules are broken, the first rule wins") {
    val bad = ok.copy(latitude = 100.0, speedKnots = -5.0)
    assert(Validation.firstProblem(bad).exists(_.contains("lat")))
  }

  // NaN fails every comparison, so it must not slip through the range checks
  test("NaN in any numeric field is rejected") {
    List(
      ok.copy(latitude = Double.NaN),
      ok.copy(longitude = Double.NaN),
      ok.copy(speedKnots = Double.NaN)
    ).foreach(p => assert(!Validation.isValid(p), s"NaN passed: $p"))
  }

  // one generated test per row
  List(
    ("latitude min", ok.copy(latitude = -90.0)),
    ("latitude max", ok.copy(latitude = 90.0)),
    ("longitude min", ok.copy(longitude = -180.0)),
    ("longitude max", ok.copy(longitude = 180.0)),
    ("speed zero", ok.copy(speedKnots = 0.0)),
    ("speed max", ok.copy(speedKnots = 60.0))
  ).foreach { (name, position) =>
    test(s"boundary is valid: $name") {
      assertEquals(Validation.firstProblem(position), None)
    }
  }
