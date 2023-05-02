package joseluisgs.dev.repositories.raquets

import joseluisgs.dev.models.Raquet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class RaquetsRepositoryImpl : RaquetsRepository {

    private val raquets = linkedMapOf<Long, Raquet>()

    override suspend fun findAll(): Flow<Raquet> = withContext(Dispatchers.IO) {
        logger.debug { "findAll" }

        return@withContext raquets.values.toList().asFlow()
    }

    override suspend fun findById(id: Long): Raquet? = withContext(Dispatchers.IO) {
        logger.debug { "findById: $id" }

        return@withContext raquets[id]
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raquet> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: $page, $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        return@withContext raquets.values.toList().subList(myOffset.toInt(), myLimit.toInt()).asFlow()

    }

    override suspend fun findByBrand(brand: String): Flow<Raquet> = withContext(Dispatchers.IO) {
        logger.debug { "findByBrand: $brand" }
        return@withContext raquets.values
            .filter { it.brand.contains(brand, true) }
            .asFlow()
    }

    override suspend fun save(entity: Raquet): Raquet = withContext(Dispatchers.IO) {
        logger.debug { "save: $entity" }

        if (entity.id == Raquet.NEW_RAQUET) {
            create(entity)
        } else {
            update(entity)
        }
    }

    private fun update(entity: Raquet): Raquet {
        logger.debug { "update: $entity" }
        raquets[entity.id] = entity
        return entity
    }

    private fun create(entity: Raquet): Raquet {
        logger.debug { "create: $entity" }
        val id = raquets.keys.maxOrNull()?.plus(1) ?: 1
        val newEntity = entity.copy(id = id)
        raquets[id] = newEntity
        return newEntity
    }

    override suspend fun delete(entity: Raquet): Raquet? {
        logger.debug { "delete: $entity" }
        return raquets.remove(entity.id)
    }

    override suspend fun deleteAll() {
        logger.debug { "deleteAll" }
        raquets.clear()
    }

    override suspend fun saveAll(entities: Iterable<Raquet>): Flow<Raquet> {
        logger.debug { "saveAll: $entities" }
        entities.forEach { save(it) }
        return entities.asFlow()
    }
}