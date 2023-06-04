package joseluisgs.dev.routes

import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import joseluisgs.dev.dto.*
import joseluisgs.dev.models.User
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.io.File
import java.util.*


private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UsersRoutesKtTest {
    // Cargamos la configuración del entorno
    private val config = ApplicationConfig("application.conf")

    val userDto = UserCreateDto(
        name = "Test",
        email = "test@test.com",
        username = "test",
        password = "test12345",
        avatar = User.DEFAULT_IMAGE,
        role = User.Role.USER
    )

    val userLoginDto = UserLoginDto(
        username = "test",
        password = "test12345"
    )

    val userLoginAdminDto = UserLoginDto(
        username = "pepe",
        password = "pepe1234"
    )

    @Test
    @Order(1)
    fun registerUserTest() = testApplication {
        // Set up the test environment
        environment { config }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Launch the test
        val response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        // Check the response and the content
        assertEquals(response.status, HttpStatusCode.Created)
        val res = json.decodeFromString<UserDto>(response.bodyAsText())
        assertAll(
            { assertEquals(res.name, userDto.name) },
            { assertEquals(res.email, userDto.email) },
            { assertEquals(res.username, userDto.username) },
            { assertEquals(res.avatar, userDto.avatar) },
            { assertEquals(res.role, userDto.role) },
        )
    }


    @Test
    @Order(2)
    fun login() = testApplication {
        environment { config }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        val responseLogin = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(responseLogin.status, HttpStatusCode.OK)
        val res = json.decodeFromString<UserWithTokenDto>(responseLogin.bodyAsText())
        assertAll(
            { assertEquals(res.user.name, userDto.name) },
            { assertEquals(res.user.email, userDto.email) },
            { assertEquals(res.user.username, userDto.username) },
            { assertEquals(res.user.avatar, userDto.avatar) },
            { assertEquals(res.user.role, userDto.role) },
            { assertNotNull(res.token) },
        )
    }

    @Test
    @Order(3)
    fun meInfoTest() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        // token
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from a local storage and return them as the 'BearerTokens' instance
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        response = client.get("/api/users/me") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(response.status, HttpStatusCode.OK)
        val resUser = json.decodeFromString<UserDto>(response.bodyAsText())
        assertAll(
            { assertEquals(resUser.name, userDto.name) },
            { assertEquals(resUser.email, userDto.email) },
            { assertEquals(resUser.username, userDto.username) },
            { assertEquals(resUser.avatar, userDto.avatar) },
            { assertEquals(resUser.role, userDto.role) },
        )
    }

    @Test
    @Order(4)
    fun meUpdateTest() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        // token
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from a local storage and return them as the 'BearerTokens' instance
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        response = client.put("/api/users/me") {
            contentType(ContentType.Application.Json)
            setBody(
                UserUpdateDto(
                    name = "Test2",
                    email = "test@test.com",
                    username = "test",
                )
            )
        }

        assertEquals(response.status, HttpStatusCode.OK)
        val resUser = json.decodeFromString<UserDto>(response.bodyAsText())
        assertAll(
            { assertEquals(resUser.name, "Test2") },
            { assertEquals(resUser.email, userDto.email) },
            { assertEquals(resUser.username, userDto.username) },
            { assertEquals(resUser.avatar, userDto.avatar) },
            { assertEquals(resUser.role, userDto.role) },
        )
    }

    @Test
    @Order(5)
    fun mePatchTest() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        // Lanzamos la consulta
        val boundary = "WebAppBoundary"
        response = client.patch("/api/users/me") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", File("src/test/resources/user.jpg").readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(HttpHeaders.ContentDisposition, "filename=\"ktor.png\"")
                        })
                    },
                    boundary,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary)
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(6)
    fun listAsAdminTestNot() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        response = client.get("/api/users/list") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(response.status, HttpStatusCode.Forbidden)
    }

    @Test
    @Order(7)
    fun deleteAsAdminTestNot() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        response = client.delete("/api/users/delete/2") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(response.status, HttpStatusCode.Forbidden)
    }

    @Test
    @Order(8)
    fun listAsAdminTestYes() = testApplication {
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }


        var response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(userLoginAdminDto)
        }

        assertEquals(response.status, HttpStatusCode.OK)

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        response = client.get("/api/users/list") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(response.status, HttpStatusCode.OK)
    }

    // You can add more tests if you want or need

}