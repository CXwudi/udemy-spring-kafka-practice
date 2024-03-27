plugins {
  id("my.kotlin-spring-kafka-app")
}

dependencies {
  implementation(project(":shared-module"))
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

application {
  mainClass.set("poc.cx.trackingapp.TrackingApplicationKt")
}