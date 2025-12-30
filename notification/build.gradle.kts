plugins {
    id("java-library")
    id("chatapp.spring-boot-service")
    alias(libs.plugins.kotlin.jpa)
}

group = "com.evandhardspace"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.amqp)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}