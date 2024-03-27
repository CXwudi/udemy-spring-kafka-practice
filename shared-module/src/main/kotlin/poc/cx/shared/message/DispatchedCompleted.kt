package poc.cx.shared.message

import java.util.*

data class DispatchCompleted(
  val orderId: UUID,
  val data: String
)
