package joseluisgs.dev.mappers

import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.dto.RacketResponse
import joseluisgs.dev.entities.RacketEntity
import joseluisgs.dev.models.Racket

/**
 * Mapper for Racquet
 * With this we can map from DTO to Model and viceversa
 * In Kotlin we can use extension functions
 */

/**
 * RacketRequest to Racket Model
 * @return Racket
 * @see Racket
 */
fun RacketRequest.toModel() = Racket(
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers,
    image = this.image
)

/*
@JvmName("fromRacketRequestListToModel")
fun List<RacketRequest>.toModel() = this.map { it.toModel() }
*/

/**
 * Racket to RacketResponse
 * @return RacketResponse
 * @see RacketResponse
 */
fun Racket.toResponse() = RacketResponse(
    id = this.id,
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers,
    image = this.image,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString(),
    isDeleted = this.isDeleted
)

/**
 * List<Racket> to List<RacketResponse>
 * @return List<RacketResponse>
 * @see RacketResponse
 */
fun List<Racket>.toResponse() = this.map { it.toResponse() }

/**
 * RacketEntity to Racket
 * @return Racket
 * @see Racket
 */
fun RacketEntity.toModel() = Racket(
    id = this.id ?: Racket.NEW_RACKET,
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers,
    image = this.image,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    isDeleted = this.isDeleted
)

//@JvmName("fromRacketEntityListToModel")
/**
 * List<RacketEntity> to List<Racket>
 * @return List<Racket>
 * @see Racket
 */
fun List<RacketEntity>.toModel() = this.map { it.toModel() }

/**
 * Racket to RacketEntity
 * @return RacketEntity
 * @see RacketEntity
 */
fun Racket.toEntity() = RacketEntity(
    id = if (this.id == Racket.NEW_RACKET) null else this.id,
    brand = this.brand,
    model = this.model,
    price = this.price,
    numberTenisPlayers = this.numberTenisPlayers,
    image = this.image,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    isDeleted = this.isDeleted
)

//fun List<Racket>.toEntity() = this.map { it.toEntity() }