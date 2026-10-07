package vessel.domain

import vessel.domain.ValidationError.{BlankVesselId, LatOutOfRange, LonOutOfRange, SpeedOutOfRange}

object ValidationV2:
  def validate(p: VesselPosition): Either[ValidationError, VesselPosition] =
    if p.vesselId.isBlank then Left(BlankVesselId)
    else if !inRange(-90.0, 90.0, p.latitude) then Left(LatOutOfRange(p.latitude))
    else if !inRange(-180.0, 180.0, p.longitude) then Left(LonOutOfRange(p.longitude))
    else if !inRange(0.0, 60.0, p.speedKnots) then Left(SpeedOutOfRange(p.speedKnots))
    else Right(p)

  def isValid(p: VesselPosition): Boolean = validate(p).isRight

  private def inRange(min: Double, max: Double, value: Double): Boolean =
    value >= min && value <= max

  def message(e: ValidationError): String = e match {
    case BlankVesselId => "vesselId must not be blank"
    case LatOutOfRange(v) => s"latitude $v must be in [-90, 90]"
    case LonOutOfRange(v) => s"longitude $v must be in [-180, 180]"
    case SpeedOutOfRange(v) => s"speed $v must be in [0, 60]"
  }
