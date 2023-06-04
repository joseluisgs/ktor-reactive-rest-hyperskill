package joseluisgs.dev.services.cache

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.dev.config.AppConfig
import joseluisgs.dev.models.Racket
import joseluisgs.dev.models.User
import org.koin.core.annotation.Singleton
import kotlin.time.Duration.Companion.seconds

/**
 * Cache Service
 * @property myConfig AppConfig Configuration of our service
 */

@Singleton
class CacheService(
    private val myConfig: AppConfig,
) {
    // Configure the Cache with the options of every entity in the cache
    val rackets by lazy {
        Cache.Builder<Long, Racket>()
            .expireAfterAccess(
                (myConfig.applicationConfiguration.property("cache.expireAfterAccess").getString()
                    .toLongOrNull())?.seconds ?: 86400.seconds
            )
            .maximumCacheSize(
                myConfig.applicationConfiguration.property("cache.maximumCacheSize").getString().toLongOrNull() ?: 1000
            )
            .build()
    }

    // by default
    val users by lazy {
        Cache.Builder<Long, User>().build()
    }
}