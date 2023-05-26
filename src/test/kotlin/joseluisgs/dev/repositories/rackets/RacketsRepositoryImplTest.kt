package joseluisgs.dev.repositories.rackets

import io.ktor.server.config.*
import joseluisgs.dev.data.racketsDemoData
import joseluisgs.dev.models.Racket
import joseluisgs.dev.services.database.DataBaseService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


/**
 * Test our Rackects Repository
 */

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RacketsRepositoryImplTest {

    val dataBaseService = DataBaseService(ApplicationConfig("application.conf"))
    val repository = RacketsRepositoryImpl(dataBaseService)


    @BeforeEach
    fun setUp() = runTest {
        // Clean and restore database with data
        dataBaseService.initDataBaseDataDemo()
    }

    @Test
    fun findAll() = runTest {
        val rackets = repository.findAll().toList()
        assertAll(
            { assertEquals(4, rackets.size) },
            { assertEquals("Babolat", rackets[0].brand) },
            { assertEquals("Babolat", rackets[1].brand) },
            { assertEquals("Head", rackets[2].brand) },
            { assertEquals("Wilson", rackets[3].brand) }
        )
    }

    @Test
    suspend fun findById() = runTest {
        val racket = repository.findById(1)!!
        assertAll(
            { assertEquals(1, racket.id) },
            { assertEquals("Babolat", racket.brand) }
        )
    }

    @Test
    fun findByIdNotFound() = runTest {
        val racket = repository.findById(-100)
        assertNull(racket)
    }

    @Test
    fun findAllPageable() = runTest {
        val rackets = repository.findAllPageable(1, 2).toList()
        assertAll(
            { assertEquals(2, rackets.size) },
            { assertEquals("Head", rackets[0].brand) },
            { assertEquals("Wilson", rackets[1].brand) }
        )
    }

    @Test
    fun findByBrand() = runTest {
        val rackets = repository.findByBrand("Babolat").toList()
        assertAll(
            { assertEquals(2, rackets.size) },
            { assertEquals("Babolat", rackets[0].brand) },
            { assertEquals("Babolat", rackets[1].brand) }
        )
    }

    @Test
    fun saveNewRacket() = runTest {
        val racket = Racket(brand = "Test Brand", model = "Test Model", price = 100.0)
        val newRacket = repository.save(racket)
        assertAll(
            { assertEquals("Test Brand", newRacket.brand) },
            { assertEquals("Test Model", newRacket.model) },
            { assertEquals(100.0, newRacket.price) }
        )
    }

    @Test
    fun saveUpdateRacket() = runTest {
        val racket = Racket(id = 1, brand = "Test Brand", model = "Test Model", price = 100.0)
        val newRacket = repository.save(racket)
        assertAll(
            { assertEquals(1, newRacket.id) },
            { assertEquals("Test Brand", newRacket.brand) },
            { assertEquals("Test Model", newRacket.model) },
            { assertEquals(100.0, newRacket.price) }
        )
    }

    @Test
    fun delete() = runTest {
        // Save a new racket
        val racket = Racket(brand = "Test Brand", model = "Test Model", price = 100.0)
        val newRacket = repository.save(racket)

        val deleted = repository.delete(newRacket)
        val exists = repository.findById(newRacket.id)
        assertAll(
            { assertEquals("Test Brand", deleted.brand) },
            { assertEquals("Test Model", deleted.model) },
            { assertEquals(100.0, deleted.price) },
            { assertNull(exists) }
        )
    }

    @Test
    fun deleteAll() = runTest {
        repository.deleteAll()
        val rackets = repository.findAll().toList()
        assertEquals(0, rackets.size)
    }

    @Test
    fun saveAll() = runTest {
        val rackets = racketsDemoData()
        val result = repository.saveAll(rackets.values).toList()
        assertAll(
            { assertEquals(rackets.size, result.size) },
            { assertEquals("Babolat", result[0].brand) },
            { assertEquals("Babolat", result[1].brand) },
            { assertEquals("Head", result[2].brand) },
            { assertEquals("Wilson", result[3].brand) }
        )
    }
}