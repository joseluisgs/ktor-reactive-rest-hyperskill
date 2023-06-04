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


/*
fun getUsuariosInit() = listOf(
    User(
        id = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        username = "pepe",
        email = "pepe@perez.com",
        password = BCrypt.hashpw("pepe1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.ADMIN
    ),
    User(
        id = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3"),
        nombre = "Ana Lopez",
        username = "ana",
        email = "ana@lopez.com",
        password = BCrypt.hashpw("ana1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.USER
    )
)
*/

