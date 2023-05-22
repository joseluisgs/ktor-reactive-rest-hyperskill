package joseluisgs.dev.routes

import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.mappers.toModel
import joseluisgs.dev.mappers.toResponse
import joseluisgs.dev.repositories.rackets.RacketsRepositoryImpl
import joseluisgs.dev.services.cache.CacheService
import joseluisgs.dev.services.database.DataBaseService
import joseluisgs.dev.services.rackets.RacketsService
import joseluisgs.dev.services.rackets.RacketsServiceImpl
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

    // Rackets Services with Repository and Cache
    val racketsService: RacketsService = RacketsServiceImpl(
        // We pass the configuration from environment or default parameter value
        RacketsRepositoryImpl(DataBaseService(environment.config)),
        CacheService(environment.config)
    )

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

                    racketsService.findAllPageable(page - 1, perPage)
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }

                    racketsService.findAll()
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                }
            }

            // Get one racquet by id --> GET /api/rackets/{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    racketsService.findById(id).mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                        failure = { handleRacketErrors(it) }
                    )
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }

            // Get one racquet by brand --> GET /api/rackets/brand/{brand}
            get("brand/{brand}") {
                logger.debug { "GET BY BRAND /$ENDPOINT/brand/{brand}" }

                val brand = call.parameters["brand"]
                brand?.let {
                    racketsService.findByBrand(it)
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Brand is not a string")
            }

            // Create a new racquet --> POST /api/rackets
            post {
                logger.debug { "POST /$ENDPOINT" }

                val racketRequest = call.receive<RacketRequest>().toModel()
                racketsService.save(racketRequest).mapBoth(
                    success = { call.respond(HttpStatusCode.Created, it.toResponse()) },
                    failure = { handleRacketErrors(it) }
                )
            }

            // Update a racquet --> PUT /api/rackets/{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    val racket = call.receive<RacketRequest>().toModel()
                    racketsService.update(id, racket).mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                        failure = { handleRacketErrors(it) }
                    )
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }

            // Delete a racquet --> DELETE /api/rackets/{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toLongOrNull()
                id?.let {
                    racketsService.delete(id).mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                        failure = { handleRacketErrors(it) }
                    )
                } ?: call.respond(HttpStatusCode.BadRequest, "ID is not a number")
            }
        }

        // WebSockets Real Time Updates and Notifications
        webSocket("/$ENDPOINT/notifications") {
            try {
                // Observer Pattern with function
                racketsService.addSuscriptor(this.hashCode()) {
                    sendSerialized(it) // Enviamos las cosas
                }
                sendSerialized("Notifications WS: Rackets - Rackets API")
                // Every time we receive a message we ignore ir, only send
                for (frame in incoming) {
                    if (frame.frameType == FrameType.CLOSE) {
                        break
                    } else if (frame is Frame.Text) {
                        logger.debug { "Mensaje recibido por WS Representantes: ${frame.readText()}" }
                    }
                }
            } finally {
                racketsService.removeSuscriptor(this.hashCode())
            }
        }
    }
}

// Error handling for our API based on the error type and message
private suspend fun PipelineContext<Unit, ApplicationCall>.handleRacketErrors(
    error: RacketError,
) {
    when (error) {
        is RacketError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is RacketError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
    }
}