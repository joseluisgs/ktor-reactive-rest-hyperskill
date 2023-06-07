package joseluisgs.dev.routes

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import joseluisgs.dev.dto.UserCreateDto
import joseluisgs.dev.dto.UserLoginDto
import joseluisgs.dev.dto.UserUpdateDto
import joseluisgs.dev.dto.UserWithTokenDto
import joseluisgs.dev.errors.storage.StorageError
import joseluisgs.dev.errors.user.UserError
import joseluisgs.dev.mappers.toDto
import joseluisgs.dev.mappers.toModel
import joseluisgs.dev.services.storage.StorageService
import joseluisgs.dev.services.users.UsersService
import joseluisgs.es.services.tokens.TokensService
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/users" // Endpoint

fun Application.usersRoutes() {

    // Dependency injection by Koin
    val usersService: UsersService by inject()
    val tokenService: TokensService by inject()
    val storageService: StorageService by inject()

    routing {
        route("/$ENDPOINT") {

            // Register a new user --> POST /api/users/register
            post("/register") {
                logger.debug { "POST Register /$ENDPOINT/register" }

                val dto = call.receive<UserCreateDto>().toModel()
                usersService.save(dto)
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.Created, it.toDto()) },
                        failure = { handleUserError(it) }
                    )
            }

            // Login a user --> POST /api/users/login
            post("/login") {
                logger.debug { "POST Login /$ENDPOINT/login" }

                val dto = call.receive<UserLoginDto>()
                usersService.checkUserNameAndPassword(dto.username, dto.password)
                    .mapBoth(
                        success = { user ->
                            val token = tokenService.generateJWT(user)
                            call.respond(HttpStatusCode.OK, UserWithTokenDto(user.toDto(), token))
                        },
                        failure = { handleUserError(it) }
                    )
            }

            // JWT Auth routes
            authenticate {
                // Get the user info --> GET /api/users/me (with token)
                get("/me") {
                    logger.debug { "GET Me /$ENDPOINT/me" }

                    // Token came with principal (authenticated) user in its claims
                    // Be careful, it comes with quotes!!!
                    val userId = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toLong()

                    usersService.findById(userId)
                        .mapBoth(
                            success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                            failure = { handleUserError(it) }
                        )
                }

                // Update user info --> PUT /api/users/me (with token)
                put("/me") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }

                    val userId = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toLong()

                    val dto = call.receive<UserUpdateDto>()

                    usersService.findById(userId).andThen {
                        usersService.update(
                            userId, it.copy(
                                name = dto.name,
                                username = dto.username,
                                email = dto.email,
                            )
                        )
                    }.mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                        failure = { handleUserError(it) }
                    )
                }

                // Update user Image --> PATCH /api/users/me (with token)
                patch("/me") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }

                    // Token came with principal (authenticated) user in its claims
                    val userId = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toLong()

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
                            // Buscar usuario,
                            storageService.saveFile(newFileName, newFileUrl, fileBytes).andThen {
                                // Actualizar usuario
                                usersService.updateImage(
                                    id = userId,
                                    image = newFileUrl
                                )
                            }.mapBoth(
                                success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                                failure = { handleUserError(it) }
                            )
                        }
                    }
                }

                // Get racket image --> GET /api/rackets/image/{image}
                get("image/{image}") {
                    logger.debug { "GET IMAGE /$ENDPOINT/image/{image}" }

                    call.parameters["image"]?.let { image ->
                        storageService.getFile(image).mapBoth(
                            success = { call.respondFile(it) },
                            failure = { handleUserError(it) }
                        )
                    }
                }

                // Get all users --> GET /api/users/list (with token and only if you are admin)
                get("/list") {
                    logger.debug { "GET Users /$ENDPOINT/list" }

                    val userId = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toLong()

                    usersService.isAdmin(userId)
                        .onSuccess {
                            usersService.findAll().toList()
                                .map { it.toDto() }
                                .let { call.respond(HttpStatusCode.OK, it) }
                        }.onFailure {
                            handleUserError(it)
                        }
                }

                // Delete user --> DELETE /api/users/delete/{id} (with token and only if you are admin)
                delete("delete/{id}") {
                    logger.debug { "DELETE User /$ENDPOINT/{id}" }

                    val userId = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toLong()

                    call.parameters["id"]?.toLong()?.let { id ->
                        usersService.isAdmin(userId).andThen {
                            usersService.findById(id).andThen { user ->
                                usersService.delete(user.id).andThen {
                                    storageService.deleteFile(it.avatar.substringAfterLast("/"))
                                }
                            }
                        }.mapBoth(
                            success = { call.respond(HttpStatusCode.NoContent) },
                            failure = { handleUserError(it) }
                        )
                    }
                }
            }
        }
    }
}

// Manejador de errores
private suspend fun PipelineContext<Unit, ApplicationCall>.handleUserError(
    error: Any
) {
    when (error) {
        // Users
        is UserError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is UserError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is UserError.Unauthorized -> call.respond(HttpStatusCode.Unauthorized, error.message)
        is UserError.Forbidden -> call.respond(HttpStatusCode.Forbidden, error.message)
        is UserError.BadCredentials -> call.respond(HttpStatusCode.BadRequest, error.message)
        is UserError.BadRole -> call.respond(HttpStatusCode.Forbidden, error.message)
        // Storage
        is StorageError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is StorageError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
    }
}