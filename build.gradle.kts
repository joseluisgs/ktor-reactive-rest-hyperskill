val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

// Logger
val micrologging_version: String by project
val logbackclassic_version: String by project

// Database
val kotysa_version: String by project
val h2_r2dbc_version: String by project

// Testing
val junit_version: String by project
val coroutines_test_version: String by project
val mockk_version: String by project

// Cache
val cache_version: String by project

// Result
val result_version: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
}

group = "joseluisgs.dev"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Content Negotiation and Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    // Content Validation
    implementation("io.ktor:ktor-server-request-validation:$ktor_version")

    // Server Status Pages
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

    // WebSockets
    implementation("io.ktor:ktor-server-websockets:$ktor_version")

    // Compression
    implementation("io.ktor:ktor-server-compression:$ktor_version")

    // Logging
    // implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("ch.qos.logback:logback-classic:$logbackclassic_version")
    implementation("io.github.microutils:kotlin-logging-jvm:$micrologging_version")

    // Database R2DBC-H2 and Kotysa
    implementation("org.ufoss.kotysa:kotysa-r2dbc:$kotysa_version")
    implementation("io.r2dbc:r2dbc-h2:$h2_r2dbc_version")

    // Cache 4K for in-memory cache
    implementation("io.github.reactivecircus.cache4k:cache4k:$cache_version")

    // Result for error handling Railway Oriented Programming
    implementation("com.michael-bull.kotlin-result:kotlin-result:$result_version")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    // testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    // JUnit 5 instead of JUnit 4
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    // To test coroutines and suspend functions
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_test_version")
    // MockK to test with mocks
    testImplementation("io.mockk:mockk:$mockk_version")

}

tasks.test {
    useJUnitPlatform()
}
