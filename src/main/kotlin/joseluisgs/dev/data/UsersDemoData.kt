package joseluisgs.dev.data

import com.toxicbakery.bcrypt.Bcrypt
import joseluisgs.dev.models.User


fun userDemoData(): MutableMap<Long, User> = mutableMapOf(
    1L to User(
        id = 1L,
        name = "Pepe Perez",
        username = "pepe",
        email = "pepe@perez.com",
        password = Bcrypt.hash("pepe1234", 12).decodeToString(),
        avatar = User.DEFAULT_IMAGE,
        role = User.Role.ADMIN
    ),
    2L to User(
        id = 2L,
        name = "Ana Lopez",
        username = "ana",
        email = "ana@lopez.com",
        password = Bcrypt.hash("ana1234", 12).decodeToString(),
        avatar = User.DEFAULT_IMAGE,
        role = User.Role.USER
    )
)
