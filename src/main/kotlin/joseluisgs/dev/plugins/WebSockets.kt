package joseluisgs.dev.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

fun Application.configureWebSockets() {
    install(WebSockets) {
        // Configure WebSockets
        // Serializer for WebSockets
        contentConverter = KotlinxWebsocketSerializationConverter(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}