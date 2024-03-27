plugins {
  id("my.mixin.kotlin-jvm")
  // although a mixin with only one usage can be inlined here,
  // but for this app mixin, I prefer to keep it in a separate file
  id("my.mixin.app")
  id("org.springframework.boot")
  kotlin("plugin.spring")
}

dependencies {
  implementation(platform("org.springframework.boot:spring-boot-dependencies")) // version came from dev-version-constraints platform
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.kafka:spring-kafka")

  implementation("com.github.CXwudi:kotlin-jvm-inline-logging")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.mockito")
  }
  testImplementation("org.springframework.kafka:spring-kafka-test")
  testImplementation("io.kotest.extensions:kotest-extensions-spring")
  testImplementation("com.ninja-squad:springmockk")
}

