package joseluisgs.dev.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.dev.dto.RacquetRequest

/**
 * A RequestValidationConfig is a class that allows you to add custom validation rules
 * to the validation plugin.
 */
fun RequestValidationConfig.racquetValidation() {
    // We add a validation rule for the RacquetRequest class
    // We can add as many as we want or need in your domain
    validate<RacquetRequest> { racquet ->
        if (racquet.brand.isBlank() || racquet.brand.length < 3) {
            ValidationResult.Invalid("Brand must be at least 3 characters long")
        } else if (racquet.model.isBlank()) {
            ValidationResult.Invalid("Model must not be empty")
        } else if (racquet.price < 0.0) {
            ValidationResult.Invalid("Price must be positive value or zero")
        } else if (racquet.numberTenisPlayers < 0) {
            ValidationResult.Invalid("Number of tenis players must be positive number or zero")
        } else {
            // Everything is ok!
            ValidationResult.Valid
        }
    }
}