package joseluisgs.dev.repositories.base

import kotlinx.coroutines.flow.Flow

/**
 * Define the CRUD operations of our application
 * based on a generic type T and ID
 * @param T Type of our entity
 * @param ID Type of our ID
 */
interface CrudRepository<T, ID> {
    suspend fun findAll(): Flow<T>
    suspend fun findById(id: ID): T?
    suspend fun save(entity: T): T
    suspend fun delete(entity: T): T
    suspend fun deleteAll()
    suspend fun saveAll(entities: Iterable<T>): Flow<T>
}