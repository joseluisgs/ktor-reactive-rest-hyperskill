package joseluisgs.dev

import io.ktor.server.application.*
import joseluisgs.dev.plugins.configureRouting
import joseluisgs.dev.plugins.configureSerialization
import joseluisgs.dev.plugins.configureStatusPages
import joseluisgs.dev.plugins.configureValidation

/**
 * Main function of our application
 */
fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


/**
 * Configure our application with the plugins
 */
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization() // Configure the serialization plugin
    configureRouting() // Configure the routing plugin
    configureValidation() // Configure the validation plugin
    configureStatusPages() // Configure the status pages plugin
}
