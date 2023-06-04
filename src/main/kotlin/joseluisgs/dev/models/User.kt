package joseluisgs.dev.models

import java.time.LocalDateTime

data class User(
    val id: Long = NEW_USER,
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String,
    val role: Role = Role.USER,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false
) {

    companion object {
        const val NEW_USER = -1L
        const val DEFAULT_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png"
    }

    enum class Role {
        USER, ADMIN
    }
}