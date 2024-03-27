package poc.cx.shared.message

import java.util.UUID

data class DispatchPreparing(
  val orderId: UUID
)
