plugins {
    // language
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)

    // framework
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)

    // testing
    alias(libs.plugins.kotest)
    jacoco
}

group = "nz.coreyh"
version = "0.0.1-SNAPSHOT"
description = "platform"

java {
    toolchain {
        languageVersion =
            JavaLanguageVersion.of(
                libs.versions.java
                    .get()
                    .toInt(),
            )
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // spring
    implementation(libs.spring.boot.starter)

    // kotlin
    implementation(libs.kotlin.reflect)

    // logging
    implementation(libs.kotlin.logging)

    // testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotest.runner.junit)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.json)
    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.mockk)
    testImplementation(libs.spring.mockk)
}

configurations.testImplementation {
    exclude(group = "org.mockito")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    executionData(tasks.test.get())
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}
