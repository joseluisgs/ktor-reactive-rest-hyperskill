package joseluisgs.dev.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.dev.dto.RacketRequest

/**
 * A RequestValidationConfig is a class that allows you to add custom validation rules
 * to the validation plugin.
 */
fun RequestValidationConfig.racketValidation() {
    // We add a validation rule for the RacquetRequest class
    // We can add as many as we want or need in your domain
    validate<RacketRequest> { racket ->
        if (racket.brand.isBlank() || racket.brand.length < 3) {
            ValidationResult.Invalid("Brand must be at least 3 characters long")
        } else if (racket.model.isBlank()) {
            ValidationResult.Invalid("Model must not be empty")
        } else if (racket.price < 0.0) {
            ValidationResult.Invalid("Price must be positive value or zero")
        } else if (racket.numberTenisPlayers < 0) {
            ValidationResult.Invalid("Number of tenis players must be positive number or zero")
        } else {
            // Everything is ok!
            ValidationResult.Valid
        }
    }
}