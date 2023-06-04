package joseluisgs.dev.services.users

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import joseluisgs.dev.data.userDemoData
import joseluisgs.dev.errors.user.UserError
import joseluisgs.dev.repositories.users.UsersRepositoryImpl
import joseluisgs.dev.services.cache.CacheService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class UsersServiceImplTest {

    @MockK
    lateinit var repository: UsersRepositoryImpl

    @MockK
    lateinit var cache: CacheService

    @InjectMockKs
    lateinit var service: UsersServiceImpl

    val users = userDemoData().values


    @Test
    fun findAll() = runTest {
        // Given
        coEvery { repository.findAll() } returns flowOf(users.first())
        // When
        val result = service.findAll().toList()
        // Then
        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(users.first(), result.first()) }
        )
        // Verifications
        coVerify { repository.findAll() }
    }

    @Test
    fun findById() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns null
        coEvery { repository.findById(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } just runs // returns Unit
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { cache.users.put(any(), users.first()) }
    }

    @Test
    fun findByIdFoundFromCache() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } returns Unit // or just runs
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { cache.users.put(any(), users.first()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns null
        coEvery { repository.findById(any()) } returns null
        // When
        val result = service.findById(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is UserError.NotFound) },
            { assertEquals(result.getError()!!.message, "User with id 1 not found") }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByUsername() = runTest {
        // Given
        coEvery { repository.findByUsername(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } returns Unit
        // When
        val result = service.findByUsername("Pepe")
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { repository.findByUsername(any()) }
        coVerify { cache.users.put(any(), users.first()) }
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        // Given
        coEvery { repository.findByUsername(any()) } returns null
        // When
        val result = service.findByUsername("Pepe")
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is UserError.NotFound) },
            { assertEquals(result.getError()!!.message, "User with username: Pepe not found") }
        )
        // Verifications
        coVerify { repository.findByUsername(any()) }
    }


    @Test
    fun checkUserNameAndPassword() = runTest {
        // Given
        coEvery { repository.checkUserNameAndPassword(any(), any()) } returns users.first()
        // When
        val result = service.checkUserNameAndPassword("test", "test1234")
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { repository.checkUserNameAndPassword(any(), any()) }
    }


    @Test
    fun checkUserNameAndPasswordNotFound() = runTest {
        // Given
        coEvery { repository.checkUserNameAndPassword(any(), any()) } returns null
        // When
        val result = service.checkUserNameAndPassword("Test", "test1234")
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is UserError.BadCredentials) },
            { assertEquals(result.getError()!!.message, "User password or username not valid") }
        )
        // Verifications
        coVerify { repository.checkUserNameAndPassword(any(), any()) }
    }

    @Test
    fun save() = runTest {
        // Given
        coEvery { repository.save(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } just runs
        coEvery { repository.hashedPassword(any()) } returns "test1234"
        // When
        val result = service.save(users.first())
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { repository.save(any()) }
        coVerify { cache.users.put(any(), users.first()) }
    }


    @Test
    fun update() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns null
        coEvery { repository.findById(any()) } returns users.first()
        coEvery { repository.save(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } just runs
        // When
        val result = service.update(1, users.first())
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { repository.save(any()) }
        coVerify { cache.users.put(any(), users.first()) }
    }
    
    @Test
    fun delete() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns null
        coEvery { repository.findById(any()) } returns users.first()
        coEvery { cache.users.put(any(), users.first()) } just runs
        coEvery { repository.delete(any()) } returns users.first()
        coEvery { cache.users.invalidate(any()) } returns Unit
        // When
        val result = service.delete(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(users.first(), result.get()) }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { repository.findById(any()) }
        coVerify { cache.users.put(any(), users.first()) }
        coVerify { repository.delete(any()) }
        coVerify { cache.users.invalidate(any()) }
    }

    @Test
    fun deleteNotFound() = runTest {
        // Given
        coEvery { cache.users.get(any()) } returns null
        coEvery { repository.findById(any()) } returns null
        // When
        val result = service.delete(1)
        // Then
        assertAll(
            { assertNotNull(result) },
            { assertNull(result.get()) },
            { assertNotNull(result.getError()) },
            { assertTrue(result.getError() is UserError.NotFound) },
            { assertEquals(result.getError()!!.message, "User with id 1 not found") }
        )
        // Verifications
        coVerify { cache.users.get(any()) }
        coVerify { repository.findById(any()) }
    }
}