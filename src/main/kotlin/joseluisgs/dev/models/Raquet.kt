package joseluisgs.dev.models

import java.time.LocalDateTime

data class Raquet(
    val id: Long = NEW_RAQUET,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val NEW_RAQUET = -1L
    }
}