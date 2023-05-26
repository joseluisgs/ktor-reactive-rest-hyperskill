package joseluisgs.dev.routes

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import joseluisgs.dev.dto.RacketPage
import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.errors.storage.StorageError
import joseluisgs.dev.mappers.toModel
import joseluisgs.dev.mappers.toResponse
import joseluisgs.dev.repositories.rackets.RacketsRepositoryImpl
import joseluisgs.dev.services.cache.CacheService
import joseluisgs.dev.services.database.DataBaseService
import joseluisgs.dev.services.rackets.RacketsService
import joseluisgs.dev.services.rackets.RacketsServiceImpl
import joseluisgs.dev.services.storage.StorageService
import joseluisgs.dev.services.storage.StorageServiceImpl
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * Rackets routes for our API
 * We define the routes for our API based on the endpoint
 * to manage the rackets
 * We use the repository to manage the data to perform the CRUD operations
 */
private const val ENDPOINT = "api/rackets"

fun Application.racketsRoutes() {

    // Rackets Services with Repository and Cache
    val racketsService: RacketsService = RacketsServiceImpl(
        // We pass the configuration from environment or default parameter value
        RacketsRepositoryImpl(DataBaseService(environment.config)),
        CacheService(environment.config),
    )

    // Storage Service
    val storageService: StorageService = StorageServiceImpl(environment.config)

    // Define routing based on endpoint
    routing {
        route("/$ENDPOINT") {

            // Get all racket --> GET /api/rackets
            get {

                // QueryParams: rackets?page=1&perPage=10
                call.request.queryParameters["page"]?.toIntOrNull()?.let {
                    val page = if (it > 0) it else 0
                    val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                    logger.debug { "GET ALL /$ENDPOINT?page=$page&perPage=$perPage" }

                    racketsService.findAllPageable(page, perPage)
                        .toList()
                        .run {
                            call.respond(HttpStatusCode.OK, RacketPage(page, perPage, this.toResponse()))
                        }

                } ?: run {
                    logger.debug { "GET ALL /$ENDPOINT" }

                    racketsService.findAll()
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                }
            }

            // Get one racket by id --> GET /api/rackets/{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }

                call.parameters["id"]?.toLong()?.let { id ->
                    racketsService.findById(id).mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                        failure = { handleRacketErrors(it) }
                    )
                }
            }

            // Get one racket by brand --> GET /api/rackets/brand/{brand}
            get("brand/{brand}") {
                logger.debug { "GET BY BRAND /$ENDPOINT/brand/{brand}" }

                call.parameters["brand"]?.let {
                    racketsService.findByBrand(it)
                        .toList()
                        .run { call.respond(HttpStatusCode.OK, this.toResponse()) }
                }
            }

            // Create a new racket --> POST /api/rackets
            post {
                logger.debug { "POST /$ENDPOINT" }

                racketsService.save(
                    racket = call.receive<RacketRequest>().toModel()
                ).mapBoth(
                    success = { call.respond(HttpStatusCode.Created, it.toResponse()) },
                    failure = { handleRacketErrors(it) }
                )
            }

            // Update a racket --> PUT /api/rackets/{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                call.parameters["id"]?.toLong()?.let { id ->
                    racketsService.update(
                        id = id,
                        racket = call.receive<RacketRequest>().toModel()
                    ).mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                        failure = { handleRacketErrors(it) }
                    )
                }
            }

            // Update a racket image --> PATCH /api/rackets/{id}
            patch("{id}") {
                logger.debug { "PATCH /$ENDPOINT/{id}" }

                call.parameters["id"]?.toLong()?.let { id ->
                    val baseUrl =
                        call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "/$ENDPOINT/image/"
                    val multipartData = call.receiveMultipart()
                    multipartData.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            val fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()
                            val fileExtension = fileName.substringAfterLast(".")
                            val newFileName = "${System.currentTimeMillis()}.$fileExtension"
                            val newFileUrl = "$baseUrl$newFileName"
                            // Save the file
                            storageService.saveFile(newFileName, newFileUrl, fileBytes).andThen {
                                // Update the racket Image
                                racketsService.updateImage(
                                    id = id,
                                    image = newFileUrl
                                )
                            }.mapBoth(
                                success = { call.respond(HttpStatusCode.OK, it.toResponse()) },
                                failure = { handleRacketErrors(it) }
                            )
                        }
                        part.dispose()
                    }
                }
            }

            // Delete a racket --> DELETE /api/rackets/{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                call.parameters["id"]?.toLong()?.let { id ->
                    racketsService.delete(id).andThen {
                        // get the racket image and delete it
                        storageService.deleteFile(it.image.substringAfterLast("/"))
                    }.mapBoth(
                        success = { call.respond(HttpStatusCode.NoContent) },
                        failure = { handleRacketErrors(it) }
                    )
                }
            }

            // Get racket image --> GET /api/rackets/image/{image}
            get("image/{image}") {
                logger.debug { "GET IMAGE /$ENDPOINT/image/{image}" }

                call.parameters["image"]?.let { image ->
                    storageService.getFile(image).mapBoth(
                        success = { call.respondFile(it) },
                        failure = { handleRacketErrors(it) }
                    )
                }
            }
        }

        // WebSockets Real Time Updates and Notifications
        webSocket("/$ENDPOINT/notifications") {

            sendSerialized("Notifications WS: Rackets - Rackets API")
            val initTime = LocalDateTime.now()
            // Remeber it will autoclose the connection, see config
            // Now we can listen and react to the changes in the StateFlow
            racketsService.notificationState
                // Sometimes we need to do something when we start
                .onStart {
                    logger.debug { "notificationState: onStart to ${this.hashCode()}" }
                    // Sometimes we need to filter any values
                }.filter {
                    // we filter the values: we only send the ones that are not empty and are after the init time
                    it.entity.isNotEmpty() && it.createdAt.isAfter(initTime)
                    // we collect the values and send them to the client
                }.collect {
                    logger.debug { "notificationState: collect $it and sent to ${this.hashCode()}" }
                    sendSerialized(it) // WS function to send the message to the client
                }
        }
    }
}

// Error handling for our API based on the error type and message
private suspend fun PipelineContext<Unit, ApplicationCall>.handleRacketErrors(
    error: Any,
) {
    // We can handle the errors in a different way
    when (error) {
        // Racket Errors
        is RacketError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is RacketError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        // Storage Errors
        is StorageError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is StorageError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
    }
}
