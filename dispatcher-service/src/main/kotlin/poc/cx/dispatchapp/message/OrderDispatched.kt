package poc.cx.dispatchapp.message

import java.util.*

data class OrderDispatched(
  val orderId: UUID,
  val processedBy: UUID,
  val notes: String
)
