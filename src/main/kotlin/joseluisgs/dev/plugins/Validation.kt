package joseluisgs.dev.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.dev.validators.racketValidation
import joseluisgs.dev.validators.userValidation

/**
 * Configure the validation plugin
 * https://ktor.io/docs/request-validation.html
 * We extend the validation with our own rules in separate file in validators package
 * like routes
 */
fun Application.configureValidation() {
    install(RequestValidation) {
        racketValidation() // Racket validation
        userValidation() // User validation
    }
}