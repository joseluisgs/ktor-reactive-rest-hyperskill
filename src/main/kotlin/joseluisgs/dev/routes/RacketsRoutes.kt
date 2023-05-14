package joseluisgs.dev.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.mappers.toModel
import joseluisgs.dev.mappers.toResponse
import joseluisgs.dev.repositories.raquets.RacketsRepository
import joseluisgs.dev.repositories.raquets.RacketsRepositoryImpl
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Racquets routes for our API
 * We define the routes for our API based on the endpoint
 * to manage the racquets
 * We use the repository to manage the data to perform the CRUD operations
 */
private const val ENDPOINT = "api/rackets"

fun Application.racketsRoutes() {

    // Repository
    val racquets: RacketsRepository = RacketsRepositoryImpl()

    // Define routing based on endpoint
    routing {
        route("/$ENDPOINT") {

            // Get all racquets --> GET /api/rackets
            get {

                // QueryParams ??
                val page = call.request.queryParameters["page"]?.toIntOrNull()
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                if (page != null && page > 0) {
                    logger.debug { "GET ALL /$ENDPOINT?page=$page&perPage=$perPage" }

                    racquets.findAllPageable(page - 1, perPage)
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }

                    racquets.findAll()
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                }
            }

            // Get one racquet by id --> GET /api/rackets/{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    racquets.findById(it)
                        ?.run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                        ?: call.respond(HttpStatusCode.NotFound, "Racket not found with ID $id")
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }

            // Get one racquet by brand --> GET /api/rackets/brand/{brand}
            get("brand/{brand}") {
                logger.debug { "GET BY BRAND /$ENDPOINT/brand/{brand}" }

                val brand = call.parameters["brand"]
                brand?.let {
                    racquets.findByBrand(it)
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Brand is not a string")
            }

            // Create a new racquet --> POST /api/rackets
            post {
                logger.debug { "POST /$ENDPOINT" }

                val racketRequest = call.receive<RacketRequest>().toModel()
                racquets.save(racketRequest)
                    .run { call.respond(HttpStatusCode.Created, this.toResponse()) }
            }

            // Update a racquet --> PUT /api/rackets/{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    val racquet = call.receive<RacketRequest>().toModel()
                    // exists?
                    racquets.findById(id)?.let {
                        racquets.save(racquet.copy(id = id))
                            .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                    } ?: call.respond(HttpStatusCode.NotFound, "Racket not found with ID $id")
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }

            // Delete a racquet --> DELETE /api/rackets/{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    // exists?
                    racquets.findById(id)?.let { racquet ->
                        racquets.delete(racquet)
                            .run { call.respond(HttpStatusCode.NoContent) }
                    } ?: call.respond(HttpStatusCode.NotFound, "Racket not found with ID $id")
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }
        }
    }
}