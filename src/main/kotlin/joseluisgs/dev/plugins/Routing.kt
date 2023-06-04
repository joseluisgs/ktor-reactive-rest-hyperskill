package joseluisgs.dev.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.dev.routes.racketsRoutes
import joseluisgs.dev.routes.usersRoutes

/**
 * Define the routing of our application based a DSL
 * https://ktor.io/docs/routing-in-ktor.html
 * we can define our routes in separate files like routes package
 */
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("\uD83D\uDC4B Hello HyperSkill Reactive API REST!")
        }
    }

    // Add our routes
    racketsRoutes() // Rackets routes
    usersRoutes() // Users routes
}
