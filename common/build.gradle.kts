plugins {
    id("java-library")
    id("chatapp.kotlin-common")
}

group = "com.evandhardspace"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jackson.module.kotlin)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}