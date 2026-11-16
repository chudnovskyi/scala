package vessel.processing

import vessel.domain.{ValidationError, ValidationV2, VesselPosition}

object Batch {
  def validateAll(ps: List[VesselPosition]): (List[ValidationError], List[VesselPosition]) =
    ps.partitionMap(ValidationV2.validate)

  def validateAllExplicitly(ps: List[VesselPosition]): (List[ValidationError], List[VesselPosition]) =
    ps.partitionMap { p =>
      ValidationV2.validate(p) match
        case Right(valid) => Right(valid)
        case Left(error) => Left(error)
    }
    
  def groupByVessel(ps: List[VesselPosition]): Map[String, List[VesselPosition]] = {
    ps.groupBy(_.vesselId).map { (name, list) => name -> list.sortBy(_.timestamp) }
  }
}
