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

  def groupByVessel(ps: List[VesselPosition]): Map[String, List[VesselPosition]] =
    ps.groupBy(_.vesselId).map { (name, list) => name -> list.sortBy(_.timestamp) }

  def averageSpeed(ps: List[VesselPosition]): Option[Double] = {
    ps match
      case Nil => None
      case list =>
        val sum = list.map(_.speedKnots).sum
        val size = list.size
        val result = sum / size
        Some(result)
  }

  def averageSpeedFold(ps: List[VesselPosition]): Option[Double] = {
    ps match
      case Nil => None
      case _ =>
        val (count, sum) = ps.map(_.speedKnots).foldLeft((0, 0.0)) {
          (acc, speed) => (acc._1 + 1, acc._2 + speed)
        }
        Some(sum / count)
  }
}
