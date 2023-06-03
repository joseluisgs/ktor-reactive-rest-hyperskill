package joseluisgs.dev.config

import io.ktor.server.config.*
import org.koin.core.annotation.Singleton

/**
 * Application Configuration to encapsulate our configuration
 * from application.conf or from other sources
 */
@Singleton
class AppConfig {
    val applicationConfiguration: ApplicationConfig = ApplicationConfig("application.conf")

    // We can set here all the configuration we want from application.conf or from other sources
    // val applicationName: String = applicationConfiguration.property("ktor.application.name").getString()
    // val applicationPort: Int = applicationConfiguration.property("ktor.application.port").getString().toInt()

}