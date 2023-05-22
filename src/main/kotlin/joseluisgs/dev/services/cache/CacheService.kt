package joseluisgs.dev.services.cache

import io.github.reactivecircus.cache4k.Cache
import io.ktor.server.config.*
import joseluisgs.dev.models.Racket
import kotlin.time.Duration.Companion.seconds

/**
 * Cache Service
 * @property cacheConfig ApplicationConfig Configuration of our cache from application.conf
 */
class CacheService(
    private val cacheConfig: ApplicationConfig = ApplicationConfig("application.conf")
) {

    // Configure the Cache with the options of every entity in the cache
    val rackets by lazy {
        Cache.Builder<Long, Racket>()
            .expireAfterAccess(
                (cacheConfig.property("cache.expireAfterAccess").getString().toLongOrNull())?.seconds ?: 86400.seconds
            )
            .maximumCacheSize(cacheConfig.property("cache.maximumCacheSize").getString().toLongOrNull() ?: 1000)
            .build()
    }
}