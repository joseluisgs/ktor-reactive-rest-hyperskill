package joseluisgs.dev.dto

import joseluisgs.dev.models.User
import kotlinx.serialization.Serializable

/**
 * User DTO for response
 */
@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val username: String,
    val avatar: String,
    val role: User.Role,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean = false
)

/**
 * User DTO for request to create a new user
 */
@Serializable
data class UserCreateDto(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String? = null,
    val role: User.Role? = User.Role.USER,
)

/**
 * User DTO for request to update a user
 */
@Serializable
data class UserUpdateDto(
    val name: String,
    val email: String,
    val username: String,
)

/**
 * User DTO for request to login a user
 */
@Serializable
data class UserLoginDto(
    val username: String,
    val password: String
)

/**
 * User DTO for response with token
 */
@Serializable
data class UserWithTokenDto(
    val user: UserDto,
    val token: String
)