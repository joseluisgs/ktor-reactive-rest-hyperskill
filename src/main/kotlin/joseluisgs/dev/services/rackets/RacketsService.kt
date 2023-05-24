package joseluisgs.dev.services.rackets

import com.github.michaelbull.result.Result
import joseluisgs.dev.dto.RacketNotification
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.models.Racket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Rackets Service to our Rackets
 * Define the CRUD operations of our application
 */
interface RacketsService {
    suspend fun findAll(): Flow<Racket>
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Racket>
    suspend fun findByBrand(brand: String): Flow<Racket>
    suspend fun findById(id: Long): Result<Racket, RacketError>
    suspend fun save(racket: Racket): Result<Racket, RacketError>
    suspend fun update(id: Long, racket: Racket): Result<Racket, RacketError>
    suspend fun delete(id: Long): Result<Racket, RacketError>

    // Real time with WebSockets. Subscribe and unsubscribe to notifications
    // See solution C
    // fun addSubscriber(id: Int, subscriber: suspend (RacketNotification) -> Unit)
    // fun removeSubscriber(id: Int)
    val notificationState: StateFlow<RacketNotification>
}