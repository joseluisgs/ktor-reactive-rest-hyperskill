package joseluisgs.dev.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*


fun Application.configureCompression() {
    // We can configure compression here
    install(Compression) {
        gzip {
            // The minimum size of a response that will be compressed
            minimumSize(512)
        }
    }
}