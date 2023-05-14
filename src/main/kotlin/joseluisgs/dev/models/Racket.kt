package joseluisgs.dev.models

import joseluisgs.dev.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
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
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false
) {
    companion object {
        val NEW_RACKET = -1L
        const val DEFAULT_IMAGE =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Tennis_Racket_and_Balls.jpg/800px-Tennis_Racket_and_Balls.jpg"
    }
}