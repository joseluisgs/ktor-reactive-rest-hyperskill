package joseluisgs.dev.services.rackets

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import joseluisgs.dev.errors.racket.RacketError
import joseluisgs.dev.models.Racket
import joseluisgs.dev.repositories.rackets.RacketsRepository
import joseluisgs.dev.services.cache.CacheService
import kotlinx.coroutines.flow.Flow
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
            cacheService.rackets.put(id, it) // save in cache
            return Ok(it)
        } ?: return Err(RacketError.NotFound("Racket with id $id not found"))

    }

    override suspend fun save(racket: Racket): Result<Racket, RacketError> {
        logger.debug { "save: save racket" }

        // save in cache and repository
        val savedRacket = racketsRepository.save(racket)
        cacheService.rackets.put(savedRacket.id, savedRacket)
        return Ok(savedRacket)
    }

    override suspend fun update(id: Long, racket: Racket): Result<Racket, RacketError> {
        logger.debug { "update: update racket" }

        // find, if exists update in cache and repository
        return findById(id).andThen {
            val updatedRacket = racketsRepository.save(it)
            cacheService.rackets.put(updatedRacket.id, updatedRacket)
            Ok(updatedRacket)
        }
    }

    override suspend fun delete(id: Long): Result<Racket, RacketError> {
        logger.debug { "delete: delete racket" }

        // find, if exists delete in cache and repository
        return findById(id).andThen {
            racketsRepository.delete(it)
            cacheService.rackets.invalidate(id)
            Ok(it)
        }
    }
}