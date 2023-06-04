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

/**
 * Users Service to our User
 * Define the CRUD operations of our application with our Users using cache
 * @property usersRepository UsersRepository Repository of our Users
 * @property cacheService CacheService Cache Service to our Users
 */
@Single
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val cacheService: CacheService
) : UsersService {

    /**
     * Find all users
     * @return Flow<User> Flow of users
     * @see User
     */
    override suspend fun findAll(): Flow<User> {
        logger.debug { "findAll: search all users" }

        return usersRepository.findAll()
    }

    /**
     * Find by id
     * @param id Long Id of user
     * @return Result<User, UserError> Result of user or error if not found
     */
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

    /**
     * Find by username
     * @param username String Username of user
     * @return Result<User, UserError> Result of user or error if not found
     */
    override suspend fun findByUsername(username: String): Result<User, UserError> {
        logger.debug { "findById: search user by username" }

        // find in cache if not found in repository
        return usersRepository.findByUsername(username)?.let { user ->
            logger.debug { "findById: found in repository" }
            cacheService.users.put(user.id, user)
            Ok(user)
        } ?: Err(UserError.NotFound("User with username: $username not found"))
    }

    /**
     * Check if username and password are valid
     * @param username String Username of user
     * @param password String Password of user
     * @return Result<User, UserError> Result of user or error if not found
     */
    override suspend fun checkUserNameAndPassword(username: String, password: String): Result<User, UserError> {
        logger.debug { "checkUserNameAndPassword: check username and password" }

        return usersRepository.checkUserNameAndPassword(username, password)?.let {
            Ok(it)
        } ?: Err(UserError.BadCredentials("User password or username not valid"))
    }

    /**
     * Save user
     * @param user User User to save
     * @return Result<User, UserError> Result of user or error if not found
     */
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

    /**
     * Update user
     * @param user User to update
     * @param id Long Id of user
     * @return Result<User, UserError> Result of user or error if not found
     */
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

    /**
     * Delete user
     * @param id Long Id of user
     * @return Result<User, UserError> Result of user or error if not found
     */
    override suspend fun delete(id: Long): Result<User, UserError> {
        logger.debug { "delete: delete" }

        // find, if exists delete in cache and repository and notify
        return findById(id).andThen {
            Ok(usersRepository.delete(it).also {
                cacheService.users.invalidate(id)
            })
        }
    }

    /**
     * Check if user is admin
     * @param id Long Id of user
     * @return Result<Boolean, UserError> Result of user or error if not found
     */
    override suspend fun isAdmin(id: Long): Result<Boolean, UserError> {
        logger.debug { "isAdmin: chek if user is admin" }
        return findById(id).andThen {
            if (it.role == User.Role.ADMIN) {
                cacheService.users.put(it.id, it)
                Ok(true)
            } else {
                Err(UserError.BadRole("User is not admin"))
            }
        }
    }

    /**
     * Update image of user
     * @param id Long Id of user
     * @param image String Image of user
     * @return Result<User, UserError> Result of user or error if not found
     */
    override suspend fun updateImage(id: Long, image: String): Result<User, UserError> {
        logger.debug { "updateImage: update image user" }

        // find, if exists update in cache and repository and notify
        return findById(id).andThen {
            Ok(usersRepository.save(
                it.copy(
                    avatar = image
                )
            ).also { res ->
                cacheService.users.put(id, res)
            })
        }
    }
}