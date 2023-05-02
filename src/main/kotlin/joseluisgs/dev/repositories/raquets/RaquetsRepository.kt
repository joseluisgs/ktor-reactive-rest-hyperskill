package joseluisgs.dev.repositories.raquets

import joseluisgs.dev.models.Raquet
import joseluisgs.dev.repositories.base.CrudRepository
import kotlinx.coroutines.flow.Flow

interface RaquetsRepository : CrudRepository<Raquet, Long> {
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Raquet>
    suspend fun findByBrand(brand: String): Flow<Raquet>
}