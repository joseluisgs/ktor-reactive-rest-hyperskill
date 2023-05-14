package joseluisgs.dev.repositories.raquets

import joseluisgs.dev.models.Racket
import joseluisgs.dev.repositories.base.CrudRepository
import kotlinx.coroutines.flow.Flow

interface RacketsRepository : CrudRepository<Racket, Long> {
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Racket>
    suspend fun findByBrand(brand: String): Flow<Racket>
}