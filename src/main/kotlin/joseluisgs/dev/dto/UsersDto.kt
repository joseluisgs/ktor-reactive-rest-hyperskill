package joseluisgs.dev.dto

import joseluisgs.dev.models.User
import kotlinx.serialization.Serializable

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

@Serializable
data class UserCreateDto(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String? = null,
    val role: User.Role? = User.Role.USER,
)

@Serializable
data class UserUpdateDto(
    val name: String,
    val email: String,
    val username: String,
)

@Serializable
data class UserLoginDto(
    val username: String,
    val password: String
)

@Serializable
data class UserWithTokenDto(
    val user: UserDto,
    val token: String
)