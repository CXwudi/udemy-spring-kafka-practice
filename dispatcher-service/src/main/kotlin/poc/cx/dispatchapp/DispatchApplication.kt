package poc.cx.dispatchapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DispatchApplication

fun main(args: Array<String>) {
  runApplication<DispatchApplication>(*args)
}
