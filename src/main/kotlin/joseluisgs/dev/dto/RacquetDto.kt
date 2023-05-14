package joseluisgs.dev.dto

import joseluisgs.dev.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * With DTO we can hide some fields from the model
 * or add some new ones o change the name of the fields or types
 * For example we can omit the Serializables for LocalDateTime using String (updatedAt)
 */

@Serializable
data class RacquetRequest(
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
)

@Serializable
data class RacquetResponse(
    val id: Long,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val updatedAt: String,
    val isDeleted: Boolean = false
)
