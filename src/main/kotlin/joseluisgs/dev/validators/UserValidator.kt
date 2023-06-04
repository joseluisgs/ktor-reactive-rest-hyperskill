package joseluisgs.dev.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.dev.dto.UserCreateDto
import joseluisgs.dev.dto.UserLoginDto
import joseluisgs.dev.dto.UserUpdateDto


fun RequestValidationConfig.userValidation() {
    // We can add as many as we want or need in your domain
    validate<UserCreateDto> { user ->
        if (user.name.isBlank()) {
            ValidationResult.Invalid("The name cannot be empty")
        } else if (user.email.isBlank()) {
            ValidationResult.Invalid("The email cannot be empty")
        } else if (!user.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            ValidationResult.Invalid("The email is not valid")
        } else if (user.username.isBlank() && user.username.length < 3) {
            ValidationResult.Invalid("The username cannot be empty or less than 3 characters")
        } else if (user.password.isBlank() || user.password.length < 7) {
            ValidationResult.Invalid("The password cannot be empty or less than 7 characters")
        } else {
            ValidationResult.Valid
        }
    }

    validate<UserUpdateDto> { user ->
        if (user.name.isBlank()) {
            ValidationResult.Invalid("The name cannot be empty")
        } else if (user.email.isBlank()) {
            ValidationResult.Invalid("The email cannot be empty")
        } else if (!user.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            ValidationResult.Invalid("The email is not valid")
        } else if (user.username.isBlank() && user.username.length < 3) {
            ValidationResult.Invalid("The username cannot be empty or less than 3 characters")
        } else {
            ValidationResult.Valid
        }
    }

    validate<UserLoginDto> { user ->
        if (user.username.isBlank()) {
            ValidationResult.Invalid("The username cannot be empty or less than 3 characters")
        } else if (user.password.isBlank() || user.password.length < 7) {
            ValidationResult.Invalid("The password cannot be empty or less than 7 characters")
        } else {
            ValidationResult.Valid
        }
    }
}