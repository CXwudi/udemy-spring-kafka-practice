package poc.cx.dispatchapp.message

import java.util.UUID

data class OrderCreated(
  val orderId: UUID,
  val item: String,
)
