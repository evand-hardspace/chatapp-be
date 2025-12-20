plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.plugin.kotlin.gradle)
    implementation(libs.plugin.kotlin.allOpen)
    implementation(libs.plugin.spring.gradle)
    implementation(libs.plugin.spring.dependencyManagement)
}
