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

// test suites
data class TestSuite(
    val test: TaskProvider<Test>,
    val jacocoReport: TaskProvider<JacocoReport>,
)

fun registerTestSuite(
    name: String,
    configure: Test.() -> Unit = {},
): TestSuite {
    val sourceSet =
        sourceSets.create(name) {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }

    configurations["${name}Implementation"].extendsFrom(configurations.testImplementation.get())
    configurations["${name}RuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

    val test =
        tasks.register<Test>(name) {
            description = "Runs $name tests."
            group = "verification"
            testClassesDirs = sourceSet.output.classesDirs
            classpath = sourceSet.runtimeClasspath
            useJUnitPlatform()
            configure()
        }

    val jacocoReport =
        tasks.register<JacocoReport>("jacoco${name.replaceFirstChar { it.uppercase() }}Report") {
            description = "Generates coverage report for the $name tests."
            group = "verification"
            dependsOn(test)
            executionData(test.get())
            sourceSets(sourceSets.main.get())
            reports {
                xml.required = true
                html.required = true
                csv.required = false
            }
        }

    test.configure { finalizedBy(jacocoReport) }

    return TestSuite(test, jacocoReport)
}

val testSuites =
    listOf(
        registerTestSuite("testUnit"),
        registerTestSuite("testIntegration") {
            systemProperty("kotest.framework.config.fqn", "nz.coreyh.linkr.support.config.KotestConfiguration")
            systemProperty("spring.profiles.active", "test")
        },
    )

dependencies {
    // spring
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)

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

    "testIntegrationImplementation"(platform(libs.test.containers.bom))
    "testIntegrationImplementation"(libs.test.containers.postgresql)
}

configurations.testImplementation {
    exclude(group = "org.mockito")
}

tasks.test {
    dependsOn(testSuites.map { it.test })
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    description = "Generates coverage report covering all test suites."
    dependsOn(testSuites.map { it.test })
    executionData(*testSuites.map { it.test.get() }.toTypedArray())
    sourceSets(sourceSets.main.get())
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}
