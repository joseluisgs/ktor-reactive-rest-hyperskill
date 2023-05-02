package joseluisgs.dev.repositories.raquets

import joseluisgs.dev.data.racquetsDemoData
import joseluisgs.dev.models.Racquet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * Repository of Raquets with CRUD operations
 */
class RacquetsRepositoryImpl : RacquetsRepository {

    private val racquets = racquetsDemoData()

    override suspend fun findAll(): Flow<Racquet> = withContext(Dispatchers.IO) {
        logger.debug { "findAll" }

        return@withContext racquets.values.toList().asFlow()
    }

    override suspend fun findById(id: Long): Racquet? = withContext(Dispatchers.IO) {
        logger.debug { "findById: $id" }

        return@withContext racquets[id]
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Racquet> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: $page, $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        return@withContext racquets.values.toList().subList(myOffset.toInt(), myLimit.toInt()).asFlow()

    }

    override suspend fun findByBrand(brand: String): Flow<Racquet> = withContext(Dispatchers.IO) {
        logger.debug { "findByBrand: $brand" }
        return@withContext racquets.values
            .filter { it.brand.contains(brand, true) }
            .asFlow()
    }

    override suspend fun save(entity: Racquet): Racquet = withContext(Dispatchers.IO) {
        logger.debug { "save: $entity" }

        if (entity.id == Racquet.NEW_RAQUET) {
            create(entity)
        } else {
            update(entity)
        }
    }

    private fun update(entity: Racquet): Racquet {
        logger.debug { "update: $entity" }
        racquets[entity.id] = entity.copy(updatedAt = LocalDateTime.now())
        return entity
    }

    private fun create(entity: Racquet): Racquet {
        logger.debug { "create: $entity" }
        val id = racquets.keys.maxOrNull()?.plus(1) ?: 1
        val newEntity = entity.copy(id = id, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        racquets[id] = newEntity
        return newEntity
    }

    override suspend fun delete(entity: Racquet): Racquet? {
        logger.debug { "delete: $entity" }
        return racquets.remove(entity.id)
    }

    override suspend fun deleteAll() {
        logger.debug { "deleteAll" }
        racquets.clear()
    }

    override suspend fun saveAll(entities: Iterable<Racquet>): Flow<Racquet> {
        logger.debug { "saveAll: $entities" }
        entities.forEach { save(it) }
        return entities.asFlow()
    }
}