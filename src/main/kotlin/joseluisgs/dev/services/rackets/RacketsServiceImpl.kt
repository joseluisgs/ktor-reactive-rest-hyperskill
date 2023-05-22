package joseluisgs.dev.services.rackets

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import joseluisgs.dev.dto.NotificacionDto.NotificationType
import joseluisgs.dev.dto.RacketNotification
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.mappers.toResponse
import joseluisgs.dev.models.Racket
import joseluisgs.dev.repositories.rackets.RacketsRepository
import joseluisgs.dev.services.cache.CacheService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Rackets Service to our Rackets
 * Define the CRUD operations of our application with our Rackets using cache
 * @property racketsRepository RacketsRepository Repository of our Rackets
 * @property cacheService CacheService Cache Service to our Rackets
 */
class RacketsServiceImpl(
    private val racketsRepository: RacketsRepository,
    private val cacheService: CacheService
) : RacketsService {
    override suspend fun findAll(): Flow<Racket> {
        logger.debug { "findAll: search all rackets" }

        return racketsRepository.findAll()
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Racket> {
        logger.debug { "findAllPageable: search all rackets with pagination" }

        return racketsRepository.findAllPageable(page, perPage)
    }

    override suspend fun findByBrand(brand: String): Flow<Racket> {
        logger.debug { "findByBrand: search all rackets by brand" }

        return racketsRepository.findByBrand(brand)
    }

    override suspend fun findById(id: Long): Result<Racket, RacketError> {
        logger.debug { "findById: search racket by id" }

        // find in cache if not found find in repository
        val racket = cacheService.rackets.get(id) ?: racketsRepository.findById(id)
        return racket?.let {
            cacheService.rackets.put(id, it) // save in cache last access
            return Ok(it)
        } ?: return Err(RacketError.NotFound("Racket with id $id not found"))

    }

    override suspend fun save(racket: Racket): Result<Racket, RacketError> {
        logger.debug { "save: save racket" }

        // save in cache and repository
        val savedRacket = racketsRepository.save(racket)
        cacheService.rackets.put(savedRacket.id, savedRacket)
        return Ok(savedRacket).also { r ->
            onChange(NotificationType.CREATE, r.value.id, r.value)
        }
    }

    override suspend fun update(id: Long, racket: Racket): Result<Racket, RacketError> {
        logger.debug { "update: update racket" }

        // find, if exists update in cache and repository
        return findById(id).andThen {
            val updatedRacket = racketsRepository.save(it)
            cacheService.rackets.put(updatedRacket.id, updatedRacket)
            Ok(updatedRacket).also { r ->
                onChange(NotificationType.UPDATE, r.value.id, r.value)
            }
        }
    }

    override suspend fun delete(id: Long): Result<Racket, RacketError> {
        logger.debug { "delete: delete racket" }

        // find, if exists delete in cache and repository
        return findById(id).andThen {
            racketsRepository.delete(it)
            cacheService.rackets.invalidate(id)
            Ok(it).also { r ->
                onChange(NotificationType.DELETE, r.value.id, r.value)
            }
        }
    }

    // Real Time Notifications and WebSockets
    // Observable Pattern
    private val suscriptors = mutableMapOf<Int, suspend (RacketNotification) -> Unit>()

    // Add and remove suscriptors
    override fun addSuscriptor(id: Int, suscriptor: suspend (RacketNotification) -> Unit) {
        logger.debug { "addSuscriptor: add suscripto with ws id: $id" }

        // Añadimos el suscriptor, que es la función que se ejecutará
        suscriptors[id] = suscriptor
    }

    override fun removeSuscriptor(id: Int) {
        logger.debug { "removeSuscriptor: Desconectando suscriptor con id: $" }

        suscriptors.remove(id)
    }

    // Event when a change is produced
    private suspend fun onChange(type: NotificationType, id: Long, data: Racket) {
        logger.debug { "onChange: Notification on Rackets: $type, notification updates to clients: $data" }

        // We use a corutine scope to execute the notification
        val myScope = CoroutineScope(Dispatchers.IO)
        // We send the notification to all suscriptors with the data
        myScope.launch {
            suscriptors.values.forEach {
                it.invoke(
                    RacketNotification(
                        entity = "RACKET",
                        type = type,
                        id = id,
                        data = data.toResponse()
                    )
                )
            }
        }
    }
}