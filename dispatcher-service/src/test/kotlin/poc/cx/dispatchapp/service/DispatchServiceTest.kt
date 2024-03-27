package poc.cx.dispatchapp.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.kafka.core.KafkaTemplate
import poc.cx.dispatchapp.message.OrderCreated
import poc.cx.dispatchapp.message.OrderDispatched
import poc.cx.dispatchapp.util.beforeGiven
import poc.cx.shared.constant.Topics
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import java.util.*
import java.util.concurrent.CompletableFuture

class DispatchServiceTest : BehaviorSpec({

  lateinit var mockedKafkaTemplate: KafkaTemplate<String, Any>
  lateinit var service: DispatchService

  beforeGiven {
    mockedKafkaTemplate = mockk<KafkaTemplate<String, Any>>(relaxed = true)
    service = DispatchService(mockedKafkaTemplate)
  }

  Given("A test created order and a mocked kafka template") {
    val testEvent = OrderCreated(UUID.randomUUID(), "test item")
    val key = UUID.randomUUID().toString()
    coEvery { mockedKafkaTemplate.send(any(), any(), any()) } returns mockk(relaxed = true) {
      coEvery { get() } returns mockk()
    }

    When("The event is processed") {
      service.process(key, testEvent)

      Then("Two dispatched events are sent") {
        coVerify(exactly = 1) { mockedKafkaTemplate.send(eq(DispatchService.ORDER_DISPATCHED_TOPIC), eq(key), ofType<OrderDispatched>()) }
        coVerify(exactly = 1) { mockedKafkaTemplate.send(eq(Topics.DISPATCH_TRACKING), eq(key), ofType<DispatchPreparing>()) }
        coVerify(exactly = 1) { mockedKafkaTemplate.send(eq(Topics.DISPATCH_TRACKING), eq(key), ofType<DispatchCompleted>()) }
      }
    }
  }

  Given("A test created order and a mocked kafka template that throws an exception") {
    val testEvent = OrderCreated(UUID.randomUUID(), "test item")
    val key = UUID.randomUUID().toString()
    coEvery { mockedKafkaTemplate.send(any(), any(), ofType<OrderDispatched>()) } throws Exception("test exception")

    When("The event is processed") {
      val throwFunc = { service.process(key, testEvent) }
      Then("An exception is thrown") {
        shouldThrow<Exception> {
          throwFunc()
        }.message shouldBe "test exception"
      }
    }
  }
})
