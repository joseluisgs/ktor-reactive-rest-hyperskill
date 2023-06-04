package joseluisgs.dev.models

import java.time.LocalDateTime

/**
 * Racket Model
 * @param id Racket ID
 * @param brand Racquet brand
 * @param model Racquet model
 * @param price Racquet price
 * @param numberTenisPlayers Number of tennis players who have used it
 * @param image Racquet image URL
 * @param createdAt Creation date
 * @param updatedAt Update date
 * @param isDeleted Is deleted
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
    /**
     * Companion object
     * @property NEW_RACKET New racket ID
     * @property DEFAULT_IMAGE Default image URL
     */
    companion object {
        const val NEW_RACKET = -1L
        const val DEFAULT_IMAGE = "https://i.imgur.com/AsZ2xYS.jpg"
    }
}