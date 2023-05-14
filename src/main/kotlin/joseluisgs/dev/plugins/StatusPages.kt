package joseluisgs.dev.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * Configure the Status Pages plugin and configure it
 * https://ktor.io/docs/status-pages.html
 * We use status pages to respond with expected exceptions
 */
fun Application.configureStatusPages() {
    // Install StatusPages plugin and configure it
    install(StatusPages) {
        // This is a custom exception we use to respond with a 400 if a validation fails
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
    }
}