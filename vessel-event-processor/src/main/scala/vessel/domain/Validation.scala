package vessel.domain

object Validation:
  def firstProblem(p: VesselPosition): Option[String] =
    if p.vesselId.isBlank then Some("vesselId must not be blank")
    else if !isWithinBoundariesIncl(-90.0, 90.0, p.latitude) then Some("lat must be in [-90, 90]")
    else if !isWithinBoundariesIncl(-180.0, 180.0, p.longitude) then Some("long must be in [-180, 180]")
    else if !isWithinBoundariesIncl(0.0, 60.0, p.speedKnots) then Some("speed must be in [0, 60]")
    else None

  def isValid(p: VesselPosition): Boolean = firstProblem(p).isEmpty

  def isWithinBoundariesIncl(p1: Double, p2: Double, param: Double): Boolean =
    param >= p1 && param <= p2
