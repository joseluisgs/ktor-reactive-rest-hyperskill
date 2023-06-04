package joseluisgs.dev.services.rackets

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import joseluisgs.dev.data.racketsDemoData
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.repositories.rackets.RacketsRepositoryImpl
import joseluisgs.dev.services.cache.CacheService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class RacketsServiceImplTest {

    @MockK
    lateinit var repository: RacketsRepositoryImpl

    @MockK
    lateinit var cache: CacheService

    @InjectMockKs
    lateinit var service: RacketsServiceImpl

    val rackets = racketsDemoData().values

    @Test
    fun findAll() = runTest {
        // Given
        coEvery { repository.findAll() } returns flowOf(rackets.first())
        // When
        val result = service.findAll().toList()
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals(rackets.first(), result.first()) }
        )
        // Verifications
        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findAllPageable() = runTest {
        // Given
        coEvery { repository.findAllPageable(0, 10) } returns flowOf(rackets.first())
        // When
        val result = service.findAllPageable(0, 10).toList()
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals(rackets.first(), result.first()) }
        )
        // Verifications
        coVerify(exactly = 1) { repository.findAllPageable(0, 10) }
    }

    @Test
    fun findByBrand() = runTest {
        // Given
        coEvery { repository.findByBrand("Babolat") } returns flowOf(rackets.first())
        // When
        val result = service.findByBrand("Babolat").toList()
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals(rackets.first(), result.first()) }
        )
        // Verifications
        coVerify(exactly = 1) { repository.findByBrand("Babolat") }

    }

    @Test
    fun findByIdFoundButNotInCache() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns rackets.first()
        coEvery { cache.rackets.put(any(), rackets.first()) } just runs // returns Unit
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(rackets.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { cache.rackets.put(any(), rackets.first()) }
    }

    @Test
    fun findByIdFoundFromCache() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns rackets.first()
        coEvery { cache.rackets.put(any(), rackets.first()) } returns Unit // or just runs
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(rackets.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { cache.rackets.put(any(), rackets.first()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns null
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is RacketError.NotFound) },
            { assertEquals(result.getError()!!.message, "Racket with id 1 not found") }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
    }

    @Test
    fun save() = runTest {
        // Given
        coEvery { repository.save(any()) } returns rackets.first()
        coEvery { cache.rackets.put(any(), rackets.first()) } just runs // returns Unit
        // When
        val result = service.save(rackets.first())
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(rackets.first(), result.get()) }
        )
        // Verifications
        coVerify { repository.save(any()) }
        coVerify { cache.rackets.put(any(), rackets.first()) }
    }

    @Test
    fun update() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns rackets.first()
        coEvery { repository.save(any()) } returns rackets.first()
        coEvery { cache.rackets.put(any(), rackets.first()) } just runs
        // When
        val result = service.update(1, rackets.first())
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(rackets.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { repository.save(any()) }
        coVerify { cache.rackets.put(any(), rackets.first()) }
    }

    @Test
    fun updateNotFound() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns null
        // When
        val result = service.update(1, rackets.first())
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is RacketError.NotFound) },
            { assertEquals(result.getError()!!.message, "Racket with id 1 not found") }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
    }

    @Test
    fun delete() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns rackets.first()
        coEvery { cache.rackets.put(any(), rackets.first()) } just runs
        coEvery { repository.delete(any()) } returns rackets.first()
        coEvery { cache.rackets.invalidate(any()) } returns Unit
        // When
        val result = service.delete(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(rackets.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { cache.rackets.put(any(), rackets.first()) }
        coVerify { repository.delete(any()) }
        coVerify { cache.rackets.invalidate(any()) }
    }

    @Test
    fun deleteNotFound() = runTest {
        // Given
        coEvery { cache.rackets.get(any()) } returns null
        coEvery { repository.findById(any()) } returns null
        // When
        val result = service.delete(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is RacketError.NotFound) },
            { assertEquals(result.getError()!!.message, "Racket with id 1 not found") }
        )
        // Verifications
        coVerify { cache.rackets.get(any()) }
        coVerify { repository.findById(any()) }
    }
}