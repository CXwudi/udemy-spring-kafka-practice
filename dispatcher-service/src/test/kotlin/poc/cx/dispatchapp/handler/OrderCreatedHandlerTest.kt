package poc.cx.dispatchapp.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import poc.cx.dispatchapp.message.OrderCreated
import poc.cx.dispatchapp.service.DispatchService
import poc.cx.dispatchapp.util.beforeGiven
import java.util.*

class OrderCreatedHandlerTest : BehaviorSpec({

  lateinit var handler: OrderCreatedHandler
  lateinit var dispatchService: DispatchService

  beforeGiven {
    dispatchService = mockk(relaxed = true)
    handler = OrderCreatedHandler(dispatchService)
  }

  Given("A test created order and a mocked dispatch service") {
    val testEvent = OrderCreated(UUID.randomUUID(), "test item")
    val randomKey = UUID.randomUUID().toString()

    When("The event is processed") {
      handler.listen(randomKey, 0, testEvent)

      Then("The dispatch service is called") {
        coVerify(exactly = 1) { dispatchService.process(randomKey, testEvent) }
      }
    }
  }

  Given("A test created order and a mocked dispatch service that throws an exception") {
    val testEvent = OrderCreated(UUID.randomUUID(), "test item")
    val key = UUID.randomUUID().toString()
    coEvery { dispatchService.process(key, any()) } throws Exception("test exception")

    When("The event is processed") {
      handler.listen(key, 0, testEvent)
      Then("An exception is thrown and captured") {
        coVerify(exactly = 1) { dispatchService.process(key, testEvent) }
      }
    }
  }
})
