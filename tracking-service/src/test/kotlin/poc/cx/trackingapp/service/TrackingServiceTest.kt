package poc.cx.trackingapp.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.kafka.core.KafkaTemplate
import poc.cx.shared.message.DispatchPreparing
import poc.cx.trackingapp.message.TrackingStatus
import poc.cx.trackingapp.message.TrackingStatusUpdated
import poc.cx.trackingapp.util.beforeGiven
import java.util.*

class TrackingServiceTest : BehaviorSpec({

  lateinit var trackingService: TrackingService
  lateinit var kafkaTemplate: KafkaTemplate<String, Any>

  beforeGiven {
    kafkaTemplate = mockk(relaxed = true)
    trackingService = TrackingService(kafkaTemplate)
  }

  Given("A dispatch preparing message and a mocked kafka template") {
    val testPreparing = DispatchPreparing(UUID.randomUUID())
    coEvery { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), any()) } returns mockk {
      coEvery { get() } returns mockk()
    }

    When("The message is processed") {
      trackingService.process(testPreparing)

      Then("A tracking status message is sent") {
        coVerify(exactly = 1) { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), ofType<TrackingStatusUpdated>()) }
      }
    }
  }

  Given("A dispatch preparing message and a mocked kafka template that throws an exception") {
    val testPreparing = DispatchPreparing(UUID.randomUUID())
    coEvery { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), any()) } throws Exception("test exception")

    When("The message is processed") {
      val throwFunc = { trackingService.process(testPreparing) }

      Then("An exception is thrown") {
        shouldThrow<Exception> {
          throwFunc()
        }.message shouldBe "test exception"
      }
    }
  }

  Given("A dispatch completed message and a mocked kafka template") {
    val testCompleted = DispatchPreparing(UUID.randomUUID())
    coEvery { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), any()) } returns mockk {
      coEvery { get() } returns mockk()
    }

    When("The message is processed") {
      trackingService.process(testCompleted)

      Then("A tracking status message is sent") {
        coVerify(exactly = 1) { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), ofType<TrackingStatusUpdated>()) }
      }
    }
  }

  Given("A dispatch completed message and a mocked kafka template that throws an exception") {
    val testCompleted = DispatchPreparing(UUID.randomUUID())
    coEvery { kafkaTemplate.send(eq(TrackingService.TRACKING_STATUS_TOPIC), any()) } throws Exception("test exception")

    When("The message is processed") {
      val throwFunc = { trackingService.process(testCompleted) }

      Then("An exception is thrown") {
        shouldThrow<Exception> {
          throwFunc()
        }.message shouldBe "test exception"
      }
    }
  }
})