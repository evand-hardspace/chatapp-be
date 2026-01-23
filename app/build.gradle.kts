import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("chatapp.spring-boot-app")
}

group = "com.evandhardspace"
version = "0.0.1-SNAPSHOT"
description = "ChatApp Backend"

tasks {
	named<BootJar>("bootJar") {
		from(project(":notification").projectDir.resolve("src/main/resources")) {
			into("")
		}
		from(project(":user").projectDir.resolve("src/main/resources")) {
			into("")
		}
	}
}

dependencies {
	implementation(projects.user)
	implementation(projects.chat)
	implementation(projects.notification)
	implementation(projects.common)

	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.data.redis)
	implementation(libs.spring.boot.starter.amqp)
	implementation(libs.spring.boot.starter.mail)
	implementation(libs.spring.boot.starter.security)
	runtimeOnly(libs.postgresql)
}
