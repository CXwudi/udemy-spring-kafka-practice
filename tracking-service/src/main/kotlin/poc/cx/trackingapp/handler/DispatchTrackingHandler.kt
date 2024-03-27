package poc.cx.trackingapp.handler

import mikufan.cx.inlinelogging.KInlineLogging
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import poc.cx.shared.constant.Topics
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import poc.cx.trackingapp.service.TrackingService

@Component
@KafkaListener(
  id = "dispatchTrackingConsumerClient",
  topics = [Topics.DISPATCH_TRACKING],
  groupId = "tracking.dispatch.tracking"
)
class DispatchTrackingHandler(
  private val trackingService: TrackingService
) {

  @KafkaHandler
  fun listenDispatchTracking(payload: DispatchPreparing) {
    try {
      trackingService.process(payload)
    } catch (e: Exception) {
      log.error(e) { "Processing failed" }
    }
  }

  @KafkaHandler
  fun listenDispatchTracking(payload: DispatchCompleted) {
    try {
      trackingService.process(payload)
    } catch (e: Exception) {
      log.error(e) { "Processing failed" }
    }
  }
}

private val log = KInlineLogging.logger()