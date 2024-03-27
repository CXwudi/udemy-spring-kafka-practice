package poc.cx.trackingapp.service

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import poc.cx.trackingapp.message.TrackingStatus
import poc.cx.trackingapp.message.TrackingStatusUpdated

@Service
class TrackingService(
  private val kafkaTemplate: KafkaTemplate<String, Any>
) {

  companion object {
    const val TRACKING_STATUS_TOPIC = "tracking.status"
  }

  fun process(payload: DispatchPreparing) {
    log.info { "Received dispatch preparing message: $payload" }
    val statusUpdated = TrackingStatusUpdated(payload.orderId, TrackingStatus.PREPARING)
    kafkaTemplate.send(TRACKING_STATUS_TOPIC, statusUpdated).get()
  }

  fun process(payload: DispatchCompleted) {
    log.info { "Received dispatch completed message: $payload" }
    val statusUpdated = TrackingStatusUpdated(payload.orderId, TrackingStatus.COMPLETED)
    kafkaTemplate.send(TRACKING_STATUS_TOPIC, statusUpdated).get()
  }
}

private val log = KInlineLogging.logger()