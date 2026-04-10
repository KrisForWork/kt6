plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "3.3.2"
    kotlin("plugin.serialization") version "2.3.20"
    application
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.ApplicationKt")
}

dependencies {
    // Core Ktor
    implementation("io.ktor:ktor-server-core-jvm:3.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:3.3.2")

    // Content Negotiation + JSON
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.2")

    // Auth + JWT
    implementation("io.ktor:ktor-server-auth-jvm:3.3.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.3.2")
    // Дополнительно: Java JWT библиотека от Auth0
    implementation("com.auth0:java-jwt:4.4.0")

    // Logging
    implementation("io.ktor:ktor-server-call-logging-jvm:3.3.2")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.3.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.3.20")
}

kotlin {
    jvmToolchain(21)
}

ktor {
    fatJar {
        archiveFileName.set("server.jar")
    }
}