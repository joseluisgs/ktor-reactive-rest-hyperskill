package joseluisgs.dev.models

import joseluisgs.dev.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Racquet model
 */
@Serializable
data class Racquet(
    val id: Long = NEW_RAQUET,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val NEW_RAQUET = -1L
    }
}