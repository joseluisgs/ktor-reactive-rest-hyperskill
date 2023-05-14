package joseluisgs.dev.entities

import joseluisgs.dev.models.Racket.Companion.DEFAULT_IMAGE
import org.ufoss.kotysa.h2.H2Table
import java.time.LocalDateTime

/**
 * Racket Entity
 */
object RacketTable : H2Table<RacketEntity>() {
    // Autoincrement and primary key
    val id = autoIncrementBigInt(RacketEntity::id).primaryKey()

    // Other fields
    val brand = varchar(RacketEntity::brand)
    val model = varchar(RacketEntity::model)
    val price = doublePrecision(RacketEntity::price)
    val numberTenisPlayers = integer(RacketEntity::numberTenisPlayers, "number_tenis_players")
    val image = varchar(RacketEntity::image, "image")

    // metadata
    val createdAt = timestamp(RacketEntity::createdAt, "created_at")
    val updatedAt = timestamp(RacketEntity::updatedAt, "updated_at")
    val isDeleted = boolean(RacketEntity::isDeleted, "is_deleted")
}


/**
 * Racket Entity
 * We can use this class to map from Entity Row to Model and viceversa
 * We use it because we can't use the same class for both (avoid id nullable)
 * Or adapt some fields type to the database
 */

data class RacketEntity(
    val id: Long?, //
    val brand: String,
    val model: String,
    val price: Double,
    val numberTenisPlayers: Int = 0,
    val image: String = DEFAULT_IMAGE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false
)
