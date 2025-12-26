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
    implementation(projects.common)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    runtimeOnly(libs.postgresql)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}