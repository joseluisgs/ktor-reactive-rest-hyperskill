package joseluisgs.dev

import io.ktor.server.application.*
import joseluisgs.dev.plugins.*

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
    configureKoin() // Configure the Koin plugin to inject dependencies
    configureSecurity() // Configure the security plugin with JWT
    configureWebSockets() // Configure the websockets plugin
    configureSerialization() // Configure the serialization plugin
    configureRouting() // Configure the routing plugin
    configureValidation() // Configure the validation plugin
    configureStatusPages() // Configure the status pages plugin
    configureCompression() // Configure the compression plugin
    configureCors() // Configure the CORS plugin
}
