package vessel

import vessel.domain.VesselPosition
import java.time.Instant

@main def run(): Unit =
  val p = VesselPosition("IMO9321483", Instant.parse("2026-01-01T00:00:00Z"), 51.9, 4.4, 12.5)
  println(p)

  val bad: Either[String, Int] = Left("not a number") // failure by convention
  println(bad)

  val good: Either[String, Int] = Right(42) // success by convention
  println(good)

  val option = good.toOption
  println(option)
