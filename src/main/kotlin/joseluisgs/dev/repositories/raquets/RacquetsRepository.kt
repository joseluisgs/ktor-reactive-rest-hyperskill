package joseluisgs.dev.repositories.raquets

import joseluisgs.dev.models.Racquet
import joseluisgs.dev.repositories.base.CrudRepository
import kotlinx.coroutines.flow.Flow

interface RacquetsRepository : CrudRepository<Racquet, Long> {
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Racquet>
    suspend fun findByBrand(brand: String): Flow<Racquet>
}