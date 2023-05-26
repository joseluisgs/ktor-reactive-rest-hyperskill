package joseluisgs.dev.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.dev.exceptions.StorageException

/**
 * Configure the Status Pages plugin and configure it
 * https://ktor.io/docs/status-pages.html
 * We use status pages to respond with expected exceptions
 */
fun Application.configureStatusPages() {
    // Install StatusPages plugin and configure it
    install(StatusPages) {

        // This is a custom exception we use to respond with a 400 if a validation fails, Bad Request
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }

        // When we try to convert a string to a number and it fails we respond with a 400 Bad Request
        exception<NumberFormatException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, "${cause.message}. The input param is not a valid number")
        }

        // Storage exception
        // Storage
        exception<StorageException.FileNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.message.toString())
        }
        exception<StorageException.FileNotSave> { call, cause ->
            call.respond(HttpStatusCode.InsufficientStorage, cause.message.toString())
        }
    }
}