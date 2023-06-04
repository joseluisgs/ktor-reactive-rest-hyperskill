package joseluisgs.es.services.tokens

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import joseluisgs.dev.config.AppConfig
import joseluisgs.dev.models.User
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Token Exception
 * @property message String
 */
sealed class TokenException(message: String) : RuntimeException(message) {
    class InvalidTokenException(message: String) : TokenException(message)
}


@Single
class TokensService(
    private val myConfig: AppConfig
) {

    val audience by lazy {
        myConfig.applicationConfiguration.propertyOrNull("jwt.audience")?.getString() ?: "jwt-audience"
    }
    val realm by lazy {
        myConfig.applicationConfiguration.propertyOrNull("jwt.realm")?.getString() ?: "jwt-realm"
    }
    private val issuer by lazy {
        myConfig.applicationConfiguration.propertyOrNull("jwt.issuer")?.getString() ?: "jwt-issuer"
    }
    private val expiresIn by lazy {
        myConfig.applicationConfiguration.propertyOrNull("jwt.tiempo")?.getString()?.toLong() ?: 3600
    }
    private val secret by lazy {
        myConfig.applicationConfiguration.propertyOrNull("jwt.secret")?.getString() ?: "jwt-secret"
    }

    init {
        logger.debug { "Iniciando servicio de tokens con audience: $audience" }
    }

    /**
     * Generate a token JWT
     * @param user User
     * @return String
     */
    fun generateJWT(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject("Authentication")
            // user claims and other data to store
            .withClaim("username", user.username)
            .withClaim("usermail", user.email)
            .withClaim("userId", user.id.toString())
            // expiration time from currentTimeMillis + (tiempo times in seconds) * 1000 (to millis)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn * 1000L))
            // sign with secret
            .sign(
                Algorithm.HMAC512(secret)
            )
    }

    /**
     * Verify a token JWT
     * @return JWTVerifier
     * @throws TokenException.InvalidTokenException
     */
    fun verifyJWT(): JWTVerifier {

        return try {
            JWT.require(Algorithm.HMAC512(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        } catch (e: Exception) {
            throw TokenException.InvalidTokenException("Invalid token")
        }
    }
}