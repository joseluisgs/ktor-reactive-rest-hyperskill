package joseluisgs.dev.mappers

import joseluisgs.dev.dto.RacquetRequest
import joseluisgs.dev.dto.RacquetResponse
import joseluisgs.dev.models.Racquet

/**
 * Mapper for Racquet
 * With this we can map from DTO to Model and viceversa
 * In Kotlin we can use extension functions
 */

fun RacquetRequest.toModel() = Racquet(
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers
)

fun List<RacquetRequest>.toModel() = this.map { it.toModel() }

fun Racquet.toResponse() = RacquetResponse(
    id = this.id,
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt.toString(),
    isDeleted = this.isDeleted
)

fun List<Racquet>.toResponse() = this.map { it.toResponse() }