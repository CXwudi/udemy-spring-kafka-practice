package poc.cx.dispatchapp.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

abstract class SpringShouldSpec(body: ShouldSpec.() -> Unit = {}) : ShouldSpec(body) {
  override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))
}

fun BehaviorSpec.beforeGiven(initialization: (TestCase) -> Unit) = beforeTest {
  if (it.name.prefix?.startsWith("Given") == true) initialization(it)
}

fun BehaviorSpec.beforeWhen(initialization: (TestCase) -> Unit) = beforeTest {
  if (it.name.prefix?.startsWith("When") == true) initialization(it)
}