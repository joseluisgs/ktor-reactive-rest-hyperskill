package joseluisgs.dev.services.users

import com.github.michaelbull.result.Result
import joseluisgs.dev.errors.user.UserError
import joseluisgs.dev.models.User
import kotlinx.coroutines.flow.Flow

interface UsersService {
    suspend fun findAll(): Flow<User>
    suspend fun findById(id: Long): Result<User, UserError>
    suspend fun findByUsername(username: String): Result<User, UserError>
    suspend fun checkUserNameAndPassword(username: String, password: String): Result<User, UserError>
    suspend fun save(user: User): Result<User, UserError>
    suspend fun update(id: Long, user: User): Result<User, UserError>
    suspend fun delete(id: Long): Result<User, UserError>
    suspend fun isAdmin(id: Long): Result<Boolean, UserError>
    suspend fun updateImage(id: Long, image: String): Result<User, UserError>
}