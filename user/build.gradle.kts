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
    implementation(libs.spring.boot.starter.data.jpa)
    runtimeOnly(libs.postgresql)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}