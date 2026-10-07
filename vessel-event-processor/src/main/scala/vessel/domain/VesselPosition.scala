package vessel.domain

import java.time.Instant

// TASK 1: this is the only domain file you start with. See TASKS.md.
case class VesselPosition(
    vesselId: String,
    timestamp: Instant,
    latitude: Double,
    longitude: Double,
    speedKnots: Double
)
