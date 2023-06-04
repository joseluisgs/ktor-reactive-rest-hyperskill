package joseluisgs.dev.errors.user

// Errores de usuario
sealed class UserError(val message: String) {
    class NotFound(message: String) : UserError(message)
    class BadRequest(message: String) : UserError(message)
    class BadCredentials(message: String) : UserError(message)
    class BadRole(message: String) : UserError(message)
    class Unauthorized(message: String) : UserError(message)
    class Forbidden(message: String) : UserError(message)
}


