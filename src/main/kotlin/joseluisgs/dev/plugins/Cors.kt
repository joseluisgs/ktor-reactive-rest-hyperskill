package joseluisgs.dev.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        anyHost() // Allow from any host
        allowHeader(HttpHeaders.ContentType) // Allow Content-Type header
        allowHeader(HttpHeaders.Authorization)
        // allowHost("client-host") // Allow requests from client-host

        // We can also specify options
        /*allowHost("client-host") // Allow requests from client-host
        allowHost("client-host:8081") // Allow requests from client-host on port 8081
        allowHost(
            "client-host",
            subDomains = listOf("en", "de", "es")
        ) // Allow requests from client-host on subdomains en, de and es
        allowHost("client-host", schemes = listOf("http", "https")) // Allow requests from client-host on http and https

        // or methods
        allowMethod(HttpMethod.Put) // Allow PUT method
        allowMethod(HttpMethod.Delete)  // Allow DELETE method*/
    }
}