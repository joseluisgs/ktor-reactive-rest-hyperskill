package joseluisgs.dev.errors.racket

sealed class RacketError(val message: String) {
    class NotFound(message: String) : RacketError(message)
    class BadRequest(message: String) : RacketError(message)
}