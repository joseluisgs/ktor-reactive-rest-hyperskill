package joseluisgs.dev.mappers

import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.entities.RacketEntity
import joseluisgs.dev.models.Racket
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class RacketMapperKtTest {
    @Test
    fun fromRequestToModel() {
        val request = RacketRequest(
            brand = "Brand",
            model = "Model",
            price = 10.0,
            numberTenisPlayers = 2,
            image = "Image"
        )
        val model = request.toModel()

        assertAll(
            { assert(model.brand == request.brand) },
            { assert(model.model == request.model) },
            { assert(model.price == request.price) },
            { assert(model.numberTenisPlayers == request.numberTenisPlayers) },
            { assert(model.image == request.image) }
        )
    }

    @Test
    fun fromModelToResponse() {
        val racket = Racket(
            brand = "Brand",
            model = "Model",
            price = 10.0,
            numberTenisPlayers = 2,
            image = "Image"
        )
        val response = racket.toResponse()

        assertAll(
            { assert(response.brand == racket.brand) },
            { assert(response.model == racket.model) },
            { assert(response.price == racket.price) },
            { assert(response.numberTenisPlayers == racket.numberTenisPlayers) },
            { assert(response.image == racket.image) }
        )
    }

    @Test
    fun testListModelToResponse() {
        val rackets = listOf(
            Racket(
                brand = "Brand",
                model = "Model",
                price = 10.0,
                numberTenisPlayers = 2,
                image = "Image"
            ),
            Racket(
                brand = "Brand",
                model = "Model",
                price = 10.0,
                numberTenisPlayers = 2,
                image = "Image"
            )
        )

        val response = rackets.toResponse()

        assertAll(
            { assert(response[0].brand == rackets[0].brand) },
            { assert(response[0].model == rackets[0].model) },
            { assert(response[0].price == rackets[0].price) },
            { assert(response[0].numberTenisPlayers == rackets[0].numberTenisPlayers) },
            { assert(response[0].image == rackets[0].image) },
            { assert(response[1].brand == rackets[1].brand) },
            { assert(response[1].model == rackets[1].model) },
            { assert(response[1].price == rackets[1].price) },
            { assert(response[1].numberTenisPlayers == rackets[1].numberTenisPlayers) },
            { assert(response[1].image == rackets[1].image) }
        )
    }

    @Test
    fun fromRacketEntityToModel() {
        val entity = RacketEntity(
            id = null,
            brand = "Brand",
            model = "Model",
            price = 10.0,
            numberTenisPlayers = 2,
            image = "Image"
        )

        val model = entity.toModel()

        assertAll(
            { assert(model.id == Racket.NEW_RACKET) },
            { assert(model.brand == entity.brand) },
            { assert(model.model == entity.model) },
            { assert(model.price == entity.price) },
            { assert(model.numberTenisPlayers == entity.numberTenisPlayers) },
            { assert(model.image == entity.image) }
        )
    }

    @Test
    fun ListEntityToModel() {
        val entities = listOf(
            RacketEntity(
                id = null,
                brand = "Brand",
                model = "Model",
                price = 10.0,
                numberTenisPlayers = 2,
                image = "Image"
            ),
            RacketEntity(
                id = 1,
                brand = "Brand",
                model = "Model",
                price = 10.0,
                numberTenisPlayers = 2,
                image = "Image"
            )
        )

        val models = entities.toModel()

        assertAll(
            { assert(models[0].id == Racket.NEW_RACKET) },
            { assert(models[0].brand == entities[0].brand) },
            { assert(models[0].model == entities[0].model) },
            { assert(models[0].price == entities[0].price) },
            { assert(models[0].numberTenisPlayers == entities[0].numberTenisPlayers) },
            { assert(models[0].image == entities[0].image) },
            { assert(models[1].id == entities[1].id) },
            { assert(models[1].brand == entities[1].brand) },
            { assert(models[1].model == entities[1].model) },
            { assert(models[1].price == entities[1].price) },
            { assert(models[1].numberTenisPlayers == entities[1].numberTenisPlayers) },
            { assert(models[1].image == entities[1].image) }
        )

    }

    @Test
    fun fromModelEntity() {
        val racket = Racket(
            brand = "Brand",
            model = "Model",
            price = 10.0,
            numberTenisPlayers = 2,
            image = "Image"
        )

        val entity = racket.toEntity()

        assertAll(
            { assert(entity.id == null) },
            { assert(entity.brand == racket.brand) },
            { assert(entity.model == racket.model) },
            { assert(entity.price == racket.price) },
            { assert(entity.numberTenisPlayers == racket.numberTenisPlayers) },
            { assert(entity.image == racket.image) }
        )
    }

}