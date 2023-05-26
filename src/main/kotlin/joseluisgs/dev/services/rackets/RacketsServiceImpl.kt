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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        return cacheService.rackets.get(id)?.let {
            logger.debug { "findById: found in cache" }
            Ok(it)
        } ?: run {
            racketsRepository.findById(id)?.let { racket ->
                logger.debug { "findById: found in repository" }
                cacheService.rackets.put(id, racket)
                Ok(racket)
            } ?: Err(RacketError.NotFound("Racket with id $id not found"))
        }
    }

    override suspend fun save(racket: Racket): Result<Racket, RacketError> {
        logger.debug { "save: save racket" }

        // return ok if we save in cache and repository and notify
        return Ok(racketsRepository.save(racket).also {
            cacheService.rackets.put(it.id, it)
            onChange(NotificationType.CREATE, it.id, it)
        })
    }

    override suspend fun update(id: Long, racket: Racket): Result<Racket, RacketError> {
        logger.debug { "update: update racket" }

        // find, if exists update in cache and repository and notify
        return findById(id).andThen {
            // Copy the new values over the existing values. Return OK if the racket was updated in the database and cache
            Ok(racketsRepository.save(
                it.copy(
                    brand = racket.brand,
                    model = racket.model,
                    price = racket.price,
                    image = racket.image,
                    numberTenisPlayers = racket.numberTenisPlayers
                )
            ).also { res ->
                cacheService.rackets.put(id, res)
                onChange(NotificationType.UPDATE, id, res)
            })
        }
    }

    override suspend fun delete(id: Long): Result<Racket, RacketError> {
        logger.debug { "delete: delete racket" }

        // find, if exists delete in cache and repository and notify
        return findById(id).andThen {
            Ok(racketsRepository.delete(it).also { res ->
                cacheService.rackets.invalidate(id)
                onChange(NotificationType.DELETE, id, res)
            })
        }
    }

    override suspend fun updateImage(id: Long, image: String): Result<Racket, RacketError> {
        logger.debug { "updateImage: update image racket" }

        // find, if exists update in cache and repository and notify
        return findById(id).andThen {
            // Copy the new values over the existing values. Return OK if the racket was updated in the database and cache
            Ok(racketsRepository.save(
                it.copy(
                    image = image
                )
            ).also { res ->
                cacheService.rackets.put(id, res)
                onChange(NotificationType.UPDATE, id, res)
            })
        }
    }

    // Real Time Notifications and WebSockets
    // We can notify use a reactive system with StateFlow
    private val _notificationState: MutableStateFlow<RacketNotification> = MutableStateFlow(
        RacketNotification(
            entity = "",
            type = NotificationType.OTHER,
            id = null,
            data = null
        )
    )
    override val notificationState: StateFlow<RacketNotification> = _notificationState

    private suspend fun onChange(type: NotificationType, id: Long, data: Racket) {
        logger.debug { "onChange: Notification on Rackets: $type, notification updates to subscribers: $data" }
        // update notification state
        _notificationState.value = RacketNotification(
            entity = "RACKET",
            type = type,
            id = id,
            data = data.toResponse()
        )
    }
}