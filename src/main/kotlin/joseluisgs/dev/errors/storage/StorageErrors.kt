package joseluisgs.dev.errors.storage

/**
 * Storage Errors
 */
sealed class StorageError(val message: String) {
    class NotFound(message: String) : StorageError(message)
    class BadRequest(message: String) : StorageError(message)
}