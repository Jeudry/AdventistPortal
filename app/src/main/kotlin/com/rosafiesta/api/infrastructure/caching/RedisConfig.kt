@file:Suppress("DEPRECATION")

package com.rosafiesta.api.infrastructure.caching

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig(){
    @Bean
    fun cacheManager(
        connectionFactory: LettuceConnectionFactory
    ): RedisCacheManager {
        // We need the polymorphic validator because we can't serialize sealed
        // classes / data classes without default typing.
        val polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("java.util.") // Allow java list
            .allowIfSubType("kotlin.collections.") // Allow kotlin list
            .allowIfSubType("com.rosafiesta.api.")
            .build()

        // Jackson 3: immutable JsonMapper built via the builder. Kotlin +
        // java.time modules are auto-registered from the classpath.
        val objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .activateDefaultTyping(polymorphicTypeValidator, DefaultTyping.NON_FINAL)
            .build()

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1L))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer(objectMapper)
                )
            )
            .disableCachingNullValues()

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .withCacheConfiguration(
                "messages",
                    cacheConfig.entryTtl(Duration.ofMinutes(30))
            )
            .transactionAware()
             .build()
    }
}