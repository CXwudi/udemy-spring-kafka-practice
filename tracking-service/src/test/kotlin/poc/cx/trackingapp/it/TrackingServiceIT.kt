package poc.cx.trackingapp.it

import mikufan.cx.inlinelogging.KInlineLogging
import org.awaitility.Awaitility
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import poc.cx.shared.constant.Topics
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import poc.cx.trackingapp.message.TrackingStatusUpdated
import poc.cx.trackingapp.service.TrackingService
import poc.cx.trackingapp.util.SpringShouldSpec
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrackingServiceIT(
  private val testKafkaListener: TestKafkaListener,
  private val kafkaTemplate: KafkaTemplate<String, Any>,
  private val embeddedKafkaBroker: EmbeddedKafkaBroker,
  private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry,
) : SpringShouldSpec({
  beforeTest {
    testKafkaListener.statusUpdatedCounter.set(0)

    kafkaListenerEndpointRegistry.listenerContainers.forEach {
      ContainerTestUtils.waitForAssignment(it, embeddedKafkaBroker.partitionsPerTopic)
    }
  }

  context("the tracking service") {
    should("send tracking status update messages, upon receiving dispatching preparing tracking message") {
      val dispatchPreparing = DispatchPreparing(UUID.randomUUID())
      kafkaTemplate.send(Topics.DISPATCH_TRACKING, dispatchPreparing).get()

      Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(Duration.ofMillis(100))
        .until { testKafkaListener.statusUpdatedCounter.get() == 1 }
    }

    should("send tracking status update messages, upon receiving dispatching completed tracking message") {
      val dispatchCompleted = DispatchCompleted(UUID.randomUUID(), "test item")
      kafkaTemplate.send(Topics.DISPATCH_TRACKING, dispatchCompleted).get()

      Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(Duration.ofMillis(100))
        .until { testKafkaListener.statusUpdatedCounter.get() == 1 }
    }
  }
}) {

  // for some reason, TestComponent is not working here, so we have to use TestConfiguration
  @TestConfiguration
  class TestConfig {
    @Bean
    fun testKafkaListener() = TestKafkaListener()
  }

  class TestKafkaListener {
    val statusUpdatedCounter: AtomicInteger = AtomicInteger(0)

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = [TrackingService.TRACKING_STATUS_TOPIC])
    fun receiveStatusUpdated(@Payload payload: TrackingStatusUpdated) {
      log.debug { "Received TrackingStatusUpdated: $payload" }
      statusUpdatedCounter.incrementAndGet()
    }
  }
}



private val log = KInlineLogging.logger()
