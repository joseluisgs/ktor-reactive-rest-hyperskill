package joseluisgs.dev.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.dev.routes.racquetsRoutes

/**
 * Define the routing of our application based a DSL
 */
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    // Add our routes
    racquetsRoutes()
}
