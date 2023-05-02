package joseluisgs.dev.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * Configure the serialization of our application based on JSON
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
