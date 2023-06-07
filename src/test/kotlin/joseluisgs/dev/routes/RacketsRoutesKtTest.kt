package joseluisgs.dev.routes

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
import joseluisgs.dev.dto.RacketRequest
import joseluisgs.dev.dto.RacketResponse
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.util.*


private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RacketsRoutesKtTest {
    // Load configuration from application.conf
    private val config = ApplicationConfig("application.conf")

    val racket = RacketRequest(
        brand = "Test",
        model = "Test",
        price = 10.0,
        numberTenisPlayers = 1,
    )

    // New we can user it to test routes with Ktor
    @Test
    @Order(1)
    fun testGetAll() = testApplication {
        // Set up the test environment
        environment { config }

        // Launch the test
        val response = client.get("/api/rackets")

        // Check the response and the content
        assertEquals(HttpStatusCode.OK, response.status)
        // Check the content if we want
        // val result = response.bodyAsText()
        // val list = json.decodeFromString<List<RacketResponse>>(result)
        // ....

    }

    @Test
    @Order(2)
    fun testGetAllPageable() = testApplication {
        environment { config }

        val response = client.get("/api/rackets?page=1&perPage=10")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(3)
    fun testPost() = testApplication {
        environment { config }

        // Configure the client, as we are going to send JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Launch the query to create it and that it is
        val response = client.post("/api/rackets") {
            contentType(ContentType.Application.Json)
            setBody(racket)
        }

        // Check that the response and the content is correct
        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.bodyAsText()

        // We can check that the result is a JSON analyzing the string or deserializing it
        val dto = json.decodeFromString<RacketResponse>(result)
        assertAll(
            { assertEquals(racket.brand, dto.brand) },
            { assertEquals(racket.model, dto.model) },
            { assertEquals(racket.price, dto.price) },
            { assertEquals(racket.numberTenisPlayers, dto.numberTenisPlayers) },
        )
    }

    @Test
    @Order(4)
    fun testPut() = testApplication {
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Create
        var response = client.post("/api/rackets") {
            contentType(ContentType.Application.Json)
            setBody(racket)
        }

        // Take the id of the result
        var dto = json.decodeFromString<RacketResponse>(response.bodyAsText())

        // Update
        response = client.put("/api/rackets/${dto.id}") {
            contentType(ContentType.Application.Json)
            setBody(racket.copy(brand = "TestBrand2", model = "TestModel2"))
        }

        // Check that the response and the content is correct
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.bodyAsText()
        dto = json.decodeFromString<RacketResponse>(result)
        assertAll(
            { assertEquals("TestBrand2", dto.brand) },
            { assertEquals("TestModel2", dto.model) },
            { assertEquals(racket.price, dto.price) },
            { assertEquals(racket.numberTenisPlayers, dto.numberTenisPlayers) },
        )
    }

    @Test
    @Order(5)
    fun testPutNotFound() = testApplication {
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.put("/api/rackets/-1") {
            contentType(ContentType.Application.Json)
            setBody(racket.copy(brand = "TestBrand2", model = "TestModel2"))
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    @Order(6)
    fun testDelete() = testApplication {
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/rackets") {
            contentType(ContentType.Application.Json)
            setBody(racket)
        }

        val dto = json.decodeFromString<RacketResponse>(response.bodyAsText())

        response = client.delete("/api/rackets/${dto.id}")
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    @Order(7)
    fun testDeleteNotFound() = testApplication {
        environment { config }

        val response = client.delete("/api/rackets/-1")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    @Order(8)
    fun testGetById() = testApplication {
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/rackets") {
            contentType(ContentType.Application.Json)
            setBody(racket)
        }

        val result = response.bodyAsText()
        var dto = json.decodeFromString<RacketResponse>(result)

        response = client.get("/api/rackets/${dto.id}")
        dto = json.decodeFromString<RacketResponse>(result)

        assertEquals(HttpStatusCode.OK, response.status)
        assertAll(
            { assertEquals(racket.brand, dto.brand) },
            { assertEquals(racket.model, dto.model) },
            { assertEquals(racket.price, dto.price) },
            { assertEquals(racket.numberTenisPlayers, dto.numberTenisPlayers) },
        )
    }

    @Test
    @Order(9)
    fun testGetByIdNotFound() = testApplication {
        environment { config }

        val response = client.get("/api/rackets/-1")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @OptIn(InternalAPI::class)
    @Test
    @Order(10)
    fun testPatchImage() = testApplication {
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/rackets") {
            contentType(ContentType.Application.Json)
            setBody(racket)
        }

        val result = response.bodyAsText()
        val dto = json.decodeFromString<RacketResponse>(result)

        val boundary = "WebAppBoundary"
        response = client.patch("/api/rackets/${dto.id}") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Load file from resources folder
                        append("file", File("src/test/resources/racket.jpg").readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/jp")
                            append(HttpHeaders.ContentDisposition, "filename=\"racket.jpg\"")
                        })
                    },
                    boundary,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary)
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

}