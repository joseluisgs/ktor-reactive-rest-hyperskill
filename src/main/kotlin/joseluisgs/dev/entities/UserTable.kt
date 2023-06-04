package joseluisgs.dev.entities

import joseluisgs.dev.models.User
import org.ufoss.kotysa.h2.H2Table
import java.time.LocalDateTime

/**
 * User Table
 */
object UserTable : H2Table<UserEntity>("users") {
    // Autoincrement and primary key
    val id = autoIncrementBigInt(UserEntity::id).primaryKey()

    // Other fields
    val nombre = varchar(UserEntity::name)
    val email = varchar(UserEntity::email)
    val username = varchar(UserEntity::username)
    val password = varchar(UserEntity::password)
    val avatar = varchar(UserEntity::avatar)
    val role = varchar(UserEntity::role)

    // metadata
    val createdAt = timestamp(UserEntity::createdAt, "created_at")
    val updatedAt = timestamp(UserEntity::updatedAt, "updated_at")
    val deleted = boolean(UserEntity::deleted)
}

/**
 * User Entity
 * We can use this class to map from Entity Row to Model and viceversa
 * We use it because we can't use the same class for both (avoid id nullable)
 * Or adapt some fields type to the database
 */
data class UserEntity(
    // Identificador
    val id: Long?, //

    // Datos
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String = User.DEFAULT_IMAGE,
    val role: String = User.Role.USER.name,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false
)