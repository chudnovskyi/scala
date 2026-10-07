package vessel

import vessel.domain.VesselPosition
import java.time.Instant

@main def run(): Unit =
  val p = VesselPosition("IMO9321483", Instant.parse("2026-01-01T00:00:00Z"), 51.9, 4.4, 12.5)
  println(p)
