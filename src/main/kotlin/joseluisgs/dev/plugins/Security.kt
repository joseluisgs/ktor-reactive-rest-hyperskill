package joseluisgs.dev.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import joseluisgs.es.services.tokens.TokenException
import joseluisgs.es.services.tokens.TokensService
import org.koin.ktor.ext.inject

// Seguridad en base a JWT
fun Application.configureSecurity() {

    // Inject the token service
    val jwtService: TokensService by inject()


    authentication {
        jwt {
            // Load the token verification config
            verifier(jwtService.verifyJWT())
            // With realm we can get the token from the request
            realm = jwtService.realm
            validate { credential ->
                // If the token is valid, it also has the indicated audience,
                // and has the user's field to compare it with the one we want
                // return the JWTPrincipal, otherwise return null
                if (credential.payload.audience.contains(jwtService.audience) &&
                    credential.payload.getClaim("username").asString().isNotEmpty()
                )
                    JWTPrincipal(credential.payload)
                else null
            }

            challenge { defaultScheme, realm ->
                throw TokenException.InvalidTokenException("Invalid or expired token")
            }
        }
    }

}
