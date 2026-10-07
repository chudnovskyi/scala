package vessel.domain

enum ValidationError:
  case BlankVesselId
  case LatOutOfRange(value: Double)
  case LonOutOfRange(value: Double)
  case SpeedOutOfRange(value: Double)
