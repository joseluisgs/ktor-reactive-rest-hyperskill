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

// Koin
val koin_ktor_version: String by project
val koin_ksp_version: String by project

// BCrypt
val bcrypt_version: String by project

// Swagger
val ktor_swagger_ui_version: String by project




plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
    // KSP for Koin Annotations
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
    // Dokka for documentation
    id("org.jetbrains.dokka") version "1.8.10"
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
    maven("https://jitpack.io") // For Swagger UI
}

dependencies {
    // *** Ktor *** //
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

    // Auth JWT
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")

    // SSL/TLS
    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")

    // CORS
    implementation("io.ktor:ktor-server-cors:$ktor_version")

    // *** Others *** //
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

    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-ktor:$koin_ktor_version") // Koit for Ktor
    implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor_version") // Koin Logger
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version") // Koin Annotations for KSP
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version") // Koin KSP Compiler for KSP

    // BCrypt
    implementation("com.ToxicBakery.library.bcrypt:bcrypt:$bcrypt_version")

    // To generate Swagger UI
    implementation("io.github.smiley4:ktor-swagger-ui:$ktor_swagger_ui_version")

    // *** Testing *** //
    // Ktor Test
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version") // For testing with Ktor Client JSON
    implementation("io.ktor:ktor-client-auth:$ktor_version") // For testing with Ktor Client Auth JWT

    // Kotlin Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

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

// Java 17
// https://kotlinlang.org/docs/get-started-with-jvm-gradle-project.html#explore-the-build-script
kotlin { // Extension for easy setup
    jvmToolchain(17) // Target version of generated JVM bytecode
}

// To generate Docker Image with JRE 17
ktor {
    docker {
        localImageName.set("hyperskill-reactive-api-kotlin-ktor")
        imageTag.set("0.0.1-preview")
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        portMappings.set(
            listOf(
                io.ktor.plugin.features.DockerPortMapping(
                    8080,
                    8080,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                ),
                io.ktor.plugin.features.DockerPortMapping(
                    8083,
                    8083,
                    io.ktor.plugin.features.DockerPortMappingProtocol.TCP
                )
            )
        )
    }
}


