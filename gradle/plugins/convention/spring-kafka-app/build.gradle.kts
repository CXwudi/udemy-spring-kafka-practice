plugins {
  `kotlin-dsl` // !id("kotlin-dsl"), this tells gradle to use kotlin dsl
}

dependencies {
  implementation(project(":mixin:app"))
  implementation(project(":mixin:kotlin-jvm"))
  implementation(libs.pluginDep.springBoot)
  implementation(libs.pluginDep.kotlinAllOpen)
}
