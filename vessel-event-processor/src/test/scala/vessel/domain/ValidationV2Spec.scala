package vessel.domain

import vessel.domain.ValidationError.{BlankVesselId, LatOutOfRange, LonOutOfRange, SpeedOutOfRange}
import vessel.domain.ValidationV2.{isValid, validate}

import java.time.Instant

class ValidationV2Spec extends munit.FunSuite:

  private val ok = VesselPosition("V1", Instant.parse("2026-01-01T00:00:00Z"), 10.0, 20.0, 5.0)

  test("a valid position is Right of the same position") {
    assertEquals(validate(ok), Right(ok))
    assert(isValid(ok))
  }

  test("blank vesselId gives BlankVesselId") {
    List("", "   ").foreach { id =>
      assertEquals(validate(ok.copy(vesselId = id)), Left(BlankVesselId))
    }
  }

  test("each out-of-range field gives its own error carrying the offending value") {
    assertEquals(validate(ok.copy(latitude = 90.1)), Left(LatOutOfRange(90.1)))
    assertEquals(validate(ok.copy(latitude = -90.1)), Left(LatOutOfRange(-90.1)))
    assertEquals(validate(ok.copy(longitude = 180.1)), Left(LonOutOfRange(180.1)))
    assertEquals(validate(ok.copy(longitude = -180.1)), Left(LonOutOfRange(-180.1)))
    assertEquals(validate(ok.copy(speedKnots = -0.1)), Left(SpeedOutOfRange(-0.1)))
    assertEquals(validate(ok.copy(speedKnots = 60.1)), Left(SpeedOutOfRange(60.1)))
  }

  test("when several rules are broken, the first rule wins") {
    val bad = ok.copy(vesselId = "", latitude = 100.0, speedKnots = -5.0)
    assertEquals(validate(bad), Left(BlankVesselId))
    assertEquals(validate(bad.copy(vesselId = "V1")), Left(LatOutOfRange(100.0)))
  }

  // NaN != NaN, so LatOutOfRange(NaN) can't be compared with assertEquals: check the shape instead
  test("NaN in any numeric field is rejected with the matching error type") {
    validate(ok.copy(latitude = Double.NaN)) match
      case Left(LatOutOfRange(v)) => assert(v.isNaN)
      case other => fail(s"unexpected: $other")
    validate(ok.copy(longitude = Double.NaN)) match
      case Left(LonOutOfRange(v)) => assert(v.isNaN)
      case other => fail(s"unexpected: $other")
    validate(ok.copy(speedKnots = Double.NaN)) match
      case Left(SpeedOutOfRange(v)) => assert(v.isNaN)
      case other => fail(s"unexpected: $other")
  }

  test("infinity is rejected") {
    assert(validate(ok.copy(speedKnots = Double.PositiveInfinity)).isLeft)
  }

  test("message mentions the field and the offending value") {
    assertEquals(ValidationV2.message(BlankVesselId), "vesselId must not be blank")
    assert(ValidationV2.message(LatOutOfRange(91.5)).contains("91.5"))
    assert(ValidationV2.message(LonOutOfRange(181.0)).startsWith("longitude"))
    assert(ValidationV2.message(SpeedOutOfRange(-1.0)).startsWith("speed"))
  }

  List(
    ("latitude min", ok.copy(latitude = -90.0)),
    ("latitude max", ok.copy(latitude = 90.0)),
    ("longitude min", ok.copy(longitude = -180.0)),
    ("longitude max", ok.copy(longitude = 180.0)),
    ("speed zero", ok.copy(speedKnots = 0.0)),
    ("speed max", ok.copy(speedKnots = 60.0))
  ).foreach { (name, position) =>
    test(s"boundary is valid: $name") {
      assertEquals(validate(position), Right(position))
    }
  }
