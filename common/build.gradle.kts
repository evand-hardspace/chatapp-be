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

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}