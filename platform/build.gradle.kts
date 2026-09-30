plugins {
    // language
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)

    // framework
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
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

    // testing
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
