package joseluisgs.dev.services.users


import com.github.michaelbull.result.*
import joseluisgs.dev.errors.user.UserError
import joseluisgs.dev.models.User
import joseluisgs.dev.repositories.users.UsersRepository
import joseluisgs.dev.services.cache.CacheService
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val cacheService: CacheService
) : UsersService {

    override suspend fun findAll(): Flow<User> {
        logger.debug { "findAll: search all users" }

        return usersRepository.findAll()
    }

    override suspend fun findById(id: Long): Result<User, UserError> {
        logger.debug { "findById: search user by id" }

        // find in cache if not found in repository
        return cacheService.users.get(id)?.let {
            logger.debug { "findById: found in cache" }
            Ok(it)
        } ?: run {
            usersRepository.findById(id)?.let { user ->
                logger.debug { "findById: found in repository" }
                cacheService.users.put(id, user)
                Ok(user)
            } ?: Err(UserError.NotFound("User with id $id not found"))
        }
    }

    override suspend fun findByUsername(username: String): Result<User, UserError> {
        logger.debug { "findById: search user by username" }

        // find in cache if not found in repository
        return usersRepository.findByUsername(username)?.let { user ->
            logger.debug { "findById: found in repository" }
            cacheService.users.put(user.id, user)
            Ok(user)
        } ?: Err(UserError.NotFound("User with username: $username not found"))
    }


    override suspend fun checkUserNameAndPassword(username: String, password: String): Result<User, UserError> {
        logger.debug { "checkUserNameAndPassword: check username and password" }

        return usersRepository.checkUserNameAndPassword(username, password)?.let {
            Ok(it)
        } ?: Err(UserError.BadCredentials("User password or username not valid"))
    }

    override suspend fun save(user: User): Result<User, UserError> {
        logger.debug { "save: save user" }

        return findByUsername(user.username).onSuccess {
            return Err(UserError.BadRequest("Another user existe with this username: ${user.username}"))
        }.onFailure {
            return Ok(usersRepository.save(user).also {
                cacheService.users.put(it.id, it)
            })
        }

    }

    override suspend fun update(id: Long, user: User): Result<User, UserError> {
        logger.debug { "update: update user" }

        // search if exists user with same username
        return findByUsername(user.username).onSuccess {
            // if exists, check if is the same user or not
            return if (user.id == id) {
                Ok(usersRepository.save(user).also { cacheService.users.put(it.id, it) })
            } else {
                Err(UserError.BadRequest("Another user exists with username: ${user.username}"))
            }
        }.onFailure {
            // if not exists, update user
            return Ok(usersRepository.save(user).also { cacheService.users.put(it.id, it) })
        }
    }

    override suspend fun delete(id: Long): Result<User, UserError> {
        logger.debug { "delete: delete" }

        // find, if exists delete in cache and repository and notify
        return findById(id).andThen {
            Ok(usersRepository.delete(it).also {
                cacheService.rackets.invalidate(id)
            })
        }
    }

    override suspend fun isAdmin(id: Long): Result<Boolean, UserError> {
        logger.debug { "isAdmin: chek if user is admin" }
        return findById(id).andThen {
            if (it.role == User.Role.ADMIN) {
                Ok(true)
            } else {
                Err(UserError.BadRole("User is not admin"))
            }
        }
    }
}