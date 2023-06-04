package joseluisgs.dev.repositories.users

import joseluisgs.dev.models.User
import joseluisgs.dev.repositories.base.CrudRepository

interface UsersRepository : CrudRepository<User, Long> {
    suspend fun findByUsername(username: String): User?
    fun hashedPassword(password: String): String
    suspend fun checkUserNameAndPassword(username: String, password: String): User?
}

