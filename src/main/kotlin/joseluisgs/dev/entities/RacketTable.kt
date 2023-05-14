package joseluisgs.dev.entities

import joseluisgs.dev.models.Racket
import org.ufoss.kotysa.h2.H2Table

/**
 * Racket Entity
 */
object RacketTable : H2Table<Racket>() {
    // Autoincrement and primary key
    val id = autoIncrementBigInt(Racket::id).primaryKey()

    // Other fields
    val brand = varchar(Racket::brand)
    val model = varchar(Racket::model)
    val price = doublePrecision(Racket::price)
    val numberTenisPlayers = integer(Racket::numberTenisPlayers, "number_tenis_players")
    val image = varchar(Racket::image, "image")

    // metadata
    val createdAt = timestamp(Racket::createdAt, "created_at")
    val updatedAt = timestamp(Racket::updatedAt, "updated_at")
    val isDeleted = boolean(Racket::isDeleted, "is_deleted")
}
