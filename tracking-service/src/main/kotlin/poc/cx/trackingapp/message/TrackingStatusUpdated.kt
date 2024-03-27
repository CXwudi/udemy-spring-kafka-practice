package poc.cx.trackingapp.message

import java.util.*

data class TrackingStatusUpdated(
  val orderId: UUID,
  val status: TrackingStatus
)

enum class TrackingStatus {
  PREPARING, COMPLETED,
}
