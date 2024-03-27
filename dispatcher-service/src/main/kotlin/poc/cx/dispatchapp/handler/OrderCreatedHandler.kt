package poc.cx.dispatchapp.handler

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import poc.cx.dispatchapp.message.OrderCreated
import poc.cx.dispatchapp.service.DispatchService

@Component
@KafkaListener(
  id = "orderConsumerClient",
  topics = [OrderCreatedHandler.ORDER_CREATED_TOPIC],
  groupId = "\${spring.kafka.consumer.group-id}"
)
class OrderCreatedHandler(
  private val dispatchService: DispatchService
) {

  companion object {
    const val ORDER_CREATED_TOPIC = "order.created"
  }

  @KafkaHandler()
  fun listen(
    @Header(KafkaHeaders.RECEIVED_KEY) key: String,
    @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
    payload: OrderCreated
  ) {
    log.info { "Received order created event: $payload by $key from partition $partition" }
    try {
      dispatchService.process(key, payload)
    } catch (e: Exception) {
      log.error(e) { "Processing failed" }
    }
  }
}

private val log = KInlineLogging.logger()