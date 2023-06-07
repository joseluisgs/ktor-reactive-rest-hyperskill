package joseluisgs.dev.repositories.users

import io.ktor.http.*
import io.ktor.server.config.*
import joseluisgs.dev.config.AppConfig
import joseluisgs.dev.models.User
import joseluisgs.dev.services.database.DataBaseService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
class UsersRepositoryImplTest {
    val dataBaseService = DataBaseService(AppConfig())
    val repository = UsersRepositoryImpl(dataBaseService)

    @BeforeEach
    fun setUp() = runTest {
        // Clean and restore database with data
        dataBaseService.initDataBaseDataDemo()
    }

    @Test
    fun findAll() = runTest {
        val result = repository.findAll().take(1).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals("Pepe Perez", result[0].name) },
            { assertEquals("pepe", result[0].username) },
            { assertEquals("pepe@perez.com", result[0].email) }
        )
    }

    @Test
    fun checkUserNameAndPassword() = runTest {
        val result = repository.checkUserNameAndPassword("pepe", "pepe1234")

        assertAll(
            { assertEquals("Pepe Perez", result?.name) },
            { assertEquals("pepe", result?.username) },
            { assertEquals("pepe@perez.com", result?.email) }
        )
    }

    @Test
    fun checkUserNameAndPasswordNotFound() = runTest {
        val result = repository.checkUserNameAndPassword("caca", "caca1234")

        assertNull(result)
    }

    @Test
    fun findById() = runTest {
        val test = User(
            name = "Test",
            username = "test",
            email = "test@test.com",
            password = "test1234",
        )

        val newUser = repository.save(test)

        val user = repository.findById(newUser.id)!!

        assertAll(
            { assertEquals(test.name, user.name) },
            { assertEquals(test.username, user.username) },
            { assertEquals(test.email, user.email) }
        )
    }

    @Test
    fun findByIdNotFound() = runTest {
        val result = repository.findById(-1)

        assertNull(result)
    }

    @Test
    fun findByUsername() = runTest {
        val result = repository.findByUsername("pepe")

        assertAll(
            { assertEquals("Pepe Perez", result?.name) },
            { assertEquals("pepe", result?.username) },
            { assertEquals("pepe@perez.com", result?.email) }
        )
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        val result = repository.findByUsername("caca")

        assertNull(result)
    }

    @Test
    fun saveNewUser() = runTest {
        val user = User(
            name = "Test",
            username = "test",
            email = "test@test.com",
            password = "test1234",
        )

        val res = repository.save(user)

        assertAll(
            { assertEquals(user.name, res.name) },
            { assertEquals(user.username, res.username) },
            { assertEquals(user.email, res.email) },
        )
    }


    @Test
    fun saveUpdateUser() = runTest {
        val user = User(
            id = 2,
            name = "Test",
            username = "test",
            email = "test@test.com",
            password = "test1234",
        )

        val res = repository.save(user)

        assertAll(
            { assertEquals(user.name, res.name) },
            { assertEquals(user.username, res.username) },
            { assertEquals(user.email, res.email) },
        )
    }

    @Test
    fun delete() = runTest {
        val user = User(
            name = "Test",
            username = "test",
            email = "test@test.com",
            password = "test1234",
        )

        val res = repository.save(user)
        repository.delete(user)
        val exists = repository.findById(user.id)

        assertAll(
            { assertEquals(user.name, res.name) },
            { assertEquals(user.username, res.username) },
            { assertEquals(user.email, res.email) },
            { assertNull(exists) }
        )
    }

}