package joseluisgs.dev.models

import joseluisgs.dev.models.User.Role.ADMIN
import joseluisgs.dev.models.User.Role.USER
import java.time.LocalDateTime

/**
 * User Model
 * @param id User ID
 * @param name User name
 * @param email User email
 * @param username User username
 * @param password User password
 * @param avatar User avatar URL
 * @param role User role
 * @param createdAt Creation date
 * @param updatedAt Update date
 * @param deleted Is deleted
 */
data class User(
    val id: Long = NEW_USER,
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String = DEFAULT_IMAGE,
    val role: Role = USER,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false
) {

    /**
     * Companion object
     * @property NEW_USER New user ID
     * @property DEFAULT_IMAGE Default image URL
     */
    companion object {
        const val NEW_USER = -1L
        const val DEFAULT_IMAGE = "https://i.imgur.com/fIgch2x.png"
    }

    /**
     * User roles
     * @property USER Normal user
     * @property ADMIN Administrator user
     */
    enum class Role {
        USER, ADMIN
    }
}