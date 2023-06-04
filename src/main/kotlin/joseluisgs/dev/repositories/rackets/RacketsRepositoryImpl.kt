package joseluisgs.dev.repositories.rackets

import joseluisgs.dev.entities.RacketTable
import joseluisgs.dev.mappers.toEntity
import joseluisgs.dev.mappers.toModel
import joseluisgs.dev.models.Racket
import joseluisgs.dev.services.database.DataBaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * Repository of Racquets with CRUD operations
 * @property dataBaseService DataBaseService
 */

@Singleton
class RacketsRepositoryImpl(
    private val dataBaseService: DataBaseService
) : RacketsRepository {

    /**
     * Find all Rackets
     * @return Flow<Racket> Flow of Rackets
     */
    override suspend fun findAll(): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findAll" }

        return@withContext (dataBaseService.client selectFrom RacketTable)
            .fetchAll().map { it.toModel() }
    }

    /**
     * Find by ID, if not exists return null
     * @param id Long ID
     * @return Racket? Racket or null
     */
    override suspend fun findById(id: Long): Racket? = withContext(Dispatchers.IO) {
        logger.debug { "findById: $id" }

        return@withContext (dataBaseService.client selectFrom RacketTable
                where RacketTable.id eq id)
            .fetchFirstOrNull()?.toModel()
    }

    /**
     * Find all Rackets with pagination
     * @param page Int Page to show
     * @param perPage Int Number of elements per page
     * @return Flow<Racket> Flow of Rackets
     */
    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: $page, $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        return@withContext (dataBaseService.client selectFrom RacketTable
                limit myLimit offset myOffset)
            .fetchAll().map { it.toModel() }

    }

    /**
     * Find by brand
     * @param brand String Brand to search
     * @return Flow<Racket> Flow of Rackets
     */
    override suspend fun findByBrand(brand: String): Flow<Racket> = withContext(Dispatchers.IO) {
        logger.debug { "findByBrand: $brand" }
        return@withContext (dataBaseService.client selectFrom RacketTable)
            .fetchAll()
            .filter { it.brand.contains(brand, true) }
            .map { it.toModel() }
    }

    /**
     * Save a Racket, if exists update, else create
     * @param entity Racket Racket to save
     * @return Racket Racket saved or updated
     */
    override suspend fun save(entity: Racket): Racket = withContext(Dispatchers.IO) {
        logger.debug { "save: $entity" }

        if (entity.id == Racket.NEW_RACKET) {
            create(entity)
        } else {
            update(entity)
        }
    }

    /**
     * Create a Racket
     * @param entity Racket to save
     * @return Racket Racket saved
     */
    private suspend fun create(entity: Racket): Racket {
        val newEntity = entity.copy(createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
            .toEntity()
        logger.debug { "create: $newEntity" }
        return (dataBaseService.client insertAndReturn newEntity).toModel()
    }

    /**
     * Update a Racket
     * @param entity Racket to update
     * @return Racket Racket updated
     */
    private suspend fun update(entity: Racket): Racket {
        logger.debug { "update: $entity" }
        val updateEntity = entity.copy(updatedAt = LocalDateTime.now()).toEntity()

        (dataBaseService.client update RacketTable
                set RacketTable.brand eq updateEntity.brand
                set RacketTable.model eq updateEntity.model
                set RacketTable.price eq updateEntity.price
                set RacketTable.numberTenisPlayers eq updateEntity.numberTenisPlayers
                set RacketTable.image eq updateEntity.image
                where RacketTable.id eq entity.id)
            .execute()
        return updateEntity.toModel()
    }

    /**
     * Delete a Racket
     * @param entity Racket to delete
     * @return Racket Racket deleted
     */
    override suspend fun delete(entity: Racket): Racket {
        logger.debug { "delete: $entity" }
        (dataBaseService.client deleteFrom RacketTable
                where RacketTable.id eq entity.id)
            .execute()
        return entity
    }

    /**
     * Delete all Rackets
     */
    override suspend fun deleteAll() {
        logger.debug { "deleteAll" }
        dataBaseService.client deleteAllFrom RacketTable
    }

    /**
     * Save all Rackets
     * @param entities Iterable<Racket> Rackets to save
     * @return Flow<Racket> Flow of Rackets
     */
    override suspend fun saveAll(entities: Iterable<Racket>): Flow<Racket> {
        logger.debug { "saveAll: $entities" }
        entities.forEach { save(it) }
        return this.findAll()
    }
}