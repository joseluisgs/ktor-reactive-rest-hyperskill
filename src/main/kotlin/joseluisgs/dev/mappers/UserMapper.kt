package joseluisgs.dev.mappers

import joseluisgs.dev.dto.UserCreateDto
import joseluisgs.dev.dto.UserDto
import joseluisgs.dev.entities.UserEntity
import joseluisgs.dev.models.User


fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        username = this.username,
        avatar = this.avatar,
        role = this.role,
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        isDeleted = this.deleted
    )
}

fun UserCreateDto.toModel(): User {
    return User(
        name = this.name,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar ?: User.DEFAULT_IMAGE,
        role = this.role ?: User.Role.USER
    )
}

fun UserEntity.toModel(): User {
    return User(
        id = this.id ?: User.NEW_USER,
        name = this.name,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar,
        role = User.Role.valueOf(this.role),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = if (this.id == User.NEW_USER) null else this.id,
        name = this.name,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar,
        role = this.role.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted
    )
}




