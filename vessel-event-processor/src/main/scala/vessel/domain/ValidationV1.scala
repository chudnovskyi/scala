package vessel.domain

object ValidationV1:
  def firstProblem(p: VesselPosition): Option[String] =
    if p.vesselId.isBlank then Some("vesselId must not be blank")
    else if !inRange(-90.0, 90.0, p.latitude) then Some("lat must be in [-90, 90]")
    else if !inRange(-180.0, 180.0, p.longitude) then Some("long must be in [-180, 180]")
    else if !inRange(0.0, 60.0, p.speedKnots) then Some("speed must be in [0, 60]")
    else None

  def isValid(p: VesselPosition): Boolean = firstProblem(p).isEmpty

  private def inRange(min: Double, max: Double, value: Double): Boolean =
    value >= min && value <= max
