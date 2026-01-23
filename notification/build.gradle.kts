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
    implementation(libs.firebase.admin.sdk)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}