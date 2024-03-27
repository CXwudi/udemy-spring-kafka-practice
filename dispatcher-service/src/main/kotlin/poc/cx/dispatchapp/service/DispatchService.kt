package poc.cx.dispatchapp.service

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import poc.cx.dispatchapp.message.OrderCreated
import poc.cx.dispatchapp.message.OrderDispatched
import poc.cx.shared.constant.Topics
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import java.util.UUID


@Service
class DispatchService(
  private val kafkaTemplate: KafkaTemplate<String, Any>
) {

  companion object {
    const val ORDER_DISPATCHED_TOPIC = "order.dispatched"
    val APP_ID = UUID.randomUUID()
  }

  fun process(key: String, payload: OrderCreated) {
    sendDispatched(key, payload)
    sendPreparing(key, payload)
    sendDispatchCompleted(key, payload)
  }

  private fun sendDispatched(key: String, payload: OrderCreated) {
    val orderDispatched = OrderDispatched(payload.orderId, APP_ID, "Dispatched: ${payload.item}")
    log.info { "Sending dispatched event: $orderDispatched by $APP_ID at key $key" }
    kafkaTemplate.send(ORDER_DISPATCHED_TOPIC, key, orderDispatched).get()
  }

  private fun sendPreparing(key: String, payload: OrderCreated) {
    val dispatchPreparing = DispatchPreparing(payload.orderId)
    kafkaTemplate.send(Topics.DISPATCH_TRACKING, key, dispatchPreparing).get()
  }

  private fun sendDispatchCompleted(key: String, payload: OrderCreated) {
    val dispatchCompleted = DispatchCompleted(payload.orderId, "Completed: ${payload.item}")
    kafkaTemplate.send(Topics.DISPATCH_TRACKING, key, dispatchCompleted).get()
  }
}

private val log = KInlineLogging.logger()