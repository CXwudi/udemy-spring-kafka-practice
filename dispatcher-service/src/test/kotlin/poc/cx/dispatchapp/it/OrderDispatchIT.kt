package poc.cx.dispatchapp.it

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import mikufan.cx.inlinelogging.KInlineLogging
import org.awaitility.Awaitility.await
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import poc.cx.dispatchapp.handler.OrderCreatedHandler
import poc.cx.dispatchapp.message.OrderCreated
import poc.cx.dispatchapp.message.OrderDispatched
import poc.cx.dispatchapp.service.DispatchService.Companion.ORDER_DISPATCHED_TOPIC
import poc.cx.dispatchapp.util.SpringShouldSpec
import poc.cx.shared.constant.Topics
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


@SpringBootTest
@ActiveProfiles("test")
// add partitions = 1 when if using Spring Boot 3.2.0, 3.2.1, and 3.2.2
// caused by https://github.com/spring-projects/spring-kafka/issues/2978
// see https://github.com/lydtechconsulting/introduction-to-kafka-with-spring-boot/wiki#error-running-integration-tests-with-spring-boot-32
@EmbeddedKafka(controlledShutdown = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderDispatchIT(
  private val orderDispatchedListener: OrderDispatchedListener,
  private val dispatchTrackingListener: DispatchTrackingListener,
  private val kafkaTemplate: KafkaTemplate<String, Any>,
  private val embeddedKafkaBroker: EmbeddedKafkaBroker,
  private val kafkaListenerEndpointRegistry: KafkaListenerEndpointRegistry,
) : SpringShouldSpec({
  beforeTest {
    dispatchTrackingListener.dispatchPreparingCounter.set(0)
    orderDispatchedListener.orderDispatchedCounter.set(0)
    dispatchTrackingListener.dispatchCompletedCounter.set(0)

    kafkaListenerEndpointRegistry.listenerContainers.forEach {
      ContainerTestUtils.waitForAssignment(it, embeddedKafkaBroker.partitionsPerTopic)
    }
  }

  context("the dispatcher service") {
    should("send dispatch preparing and order dispatched messages, upon receiving order created message") {
      val orderCreated = OrderCreated(UUID.randomUUID(), "an item")
      val key = UUID.randomUUID().toString()
      kafkaTemplate.send(OrderCreatedHandler.ORDER_CREATED_TOPIC, key, orderCreated).get()

      await().atMost(3, TimeUnit.SECONDS).pollDelay(Duration.ofMillis(100))
        .until { dispatchTrackingListener.dispatchPreparingCounter.get() == 1 }
      await().atMost(1, TimeUnit.SECONDS).pollDelay(Duration.ofMillis(100))
        .until { orderDispatchedListener.orderDispatchedCounter.get() == 1 }
      await().atMost(1, TimeUnit.SECONDS).pollDelay(Duration.ofMillis(100))
        .until { dispatchTrackingListener.dispatchCompletedCounter.get() == 1 }
    }
  }
}) {

  // for some reason, TestComponent is not working here, so we have to use TestConfiguration
  @TestConfiguration
  class TestConfig {
    @Bean
    fun orderDispatchedListener() = OrderDispatchedListener()

    @Bean
    fun dispatchTrackingListener() = DispatchTrackingListener()
  }

  class OrderDispatchedListener {
    val orderDispatchedCounter: AtomicInteger = AtomicInteger(0)

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = [ORDER_DISPATCHED_TOPIC])
    fun receiveOrderDispatched(
      @Header(KafkaHeaders.RECEIVED_KEY) key: String,
      @Payload payload: OrderDispatched
    ) {
      log.debug { "Received OrderDispatched: $payload with key $key" }
      key shouldNot beBlank()
      payload shouldNot beNull()
      orderDispatchedCounter.incrementAndGet()
    }
  }

  @KafkaListener(groupId = "KafkaIntegrationTest", topics = [Topics.DISPATCH_TRACKING])
  class DispatchTrackingListener {

    val dispatchPreparingCounter: AtomicInteger = AtomicInteger(0)
    val dispatchCompletedCounter: AtomicInteger = AtomicInteger(0)

    @KafkaHandler
    fun receiveDispatchPreparing(
      @Header(KafkaHeaders.RECEIVED_KEY) key: String,
      @Payload payload: DispatchPreparing
    ) {
      log.debug { "Received DispatchPreparing: $payload with key $key" }
      key shouldNot beBlank()
      payload shouldNot beNull()
      dispatchPreparingCounter.incrementAndGet()
    }

    @KafkaHandler
    fun receiveDispatchCompleted(
      @Header(KafkaHeaders.RECEIVED_KEY) key: String,
      @Payload payload: DispatchCompleted
    ) {
      log.debug { "Received DispatchCompleted: $payload with key $key" }
      key shouldNot beBlank()
      payload shouldNot beNull()
      dispatchCompletedCounter.incrementAndGet()
    }
  }
}


private val log = KInlineLogging.logger()