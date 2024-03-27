package poc.cx.trackingapp.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import poc.cx.shared.message.DispatchCompleted
import poc.cx.shared.message.DispatchPreparing
import poc.cx.trackingapp.service.TrackingService
import poc.cx.trackingapp.util.beforeWhen
import java.util.*

class DispatchTrackingHandlerTest : BehaviorSpec({

  lateinit var handler: DispatchTrackingHandler
  lateinit var service: TrackingService

  beforeWhen {
    service = mockk(relaxed = true)
    handler = DispatchTrackingHandler(service)
  }

  Given("A tracking handler and a mocked tracking service") {
    When("The dispatch preparing event is processed normally") {
      coEvery { service.process(ofType<DispatchPreparing>()) } returns Unit

      handler.listenDispatchTracking(mockk<DispatchPreparing>())

      Then("The tracking service is called") {
        coVerify(exactly = 1) { service.process(ofType<DispatchPreparing>()) }
      }
    }

    When("The dispatch preparing event is processed with an exception") {
      coEvery { service.process(ofType<DispatchCompleted>()) } throws RuntimeException("test")

      handler.listenDispatchTracking(mockk<DispatchCompleted>())

      Then("An exception is thrown and captured") {
        coVerify(exactly = 1) { service.process(ofType<DispatchCompleted>()) }
      }
    }

    When("The dispatch completed event is processed normally") {
      coEvery { service.process(ofType<DispatchCompleted>()) } returns Unit

      handler.listenDispatchTracking(mockk<DispatchCompleted>())

      Then("The tracking service is called") {
        coVerify(exactly = 1) { service.process(ofType<DispatchCompleted>()) }
      }
    }

    When("The dispatch completed event is processed with an exception") {
      coEvery { service.process(ofType<DispatchCompleted>()) } throws RuntimeException("test")

      handler.listenDispatchTracking(mockk<DispatchCompleted>())

      Then("An exception is thrown and captured") {
        coVerify(exactly = 1) { service.process(ofType<DispatchCompleted>()) }
      }
    }
  }
})