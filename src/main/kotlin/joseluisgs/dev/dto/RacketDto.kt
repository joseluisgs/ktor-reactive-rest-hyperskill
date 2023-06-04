package joseluisgs.dev.dto

import joseluisgs.dev.models.Racket.Companion.DEFAULT_IMAGE
import kotlinx.serialization.Serializable


/**
 * With DTO we can hide some fields from the model
 * or add some new ones o change the name of the fields or types
 * For example we can omit the Serializables for LocalDateTime using String (updatedAt)
 */

@Serializable
data class RacketRequest(
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
    val image: String = DEFAULT_IMAGE,
)

@Serializable
data class RacketResponse(
    val id: Long,
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean = false
)

@Serializable
data class RacketPage(
    val page: Int,
    val perPage: Int,
    val data: List<RacketResponse>
)
