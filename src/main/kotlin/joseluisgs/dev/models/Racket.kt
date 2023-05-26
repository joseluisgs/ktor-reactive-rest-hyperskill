package joseluisgs.dev.models

import java.time.LocalDateTime

/**
 * Racquet model
 */

data class Racket(
    val id: Long = NEW_RACKET,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
    val image: String = DEFAULT_IMAGE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false
) {
    companion object {
        val NEW_RACKET = -1L
        const val DEFAULT_IMAGE = "https://i.imgur.com/AsZ2xYS.jpg"
    }
}