package com.adventistportal.chat.infra.configs

import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.core.api.serialization.apiSerializersModule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import java.time.Duration

/**
 * The message-page cache.
 *
 * The previous version used Jackson with polymorphic default typing, which stores the
 * Kotlin class name in the value: renaming a package invalidated every entry, and the
 * type name in the payload is a deserialisation gadget. What goes in now is the one type
 * that is actually cached, named by the code rather than by the data.
 */
@Configuration
@EnableCaching
class CacheConfig {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = apiSerializersModule
    }

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(TTL_MINUTES))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(messagePageSerializer()),
            )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(configuration)
            .build()
    }

    private fun messagePageSerializer(): RedisSerializer<Any> {
        val pageSerializer = ListSerializer(ChatMessage.serializer())
        return object : RedisSerializer<Any> {
            @Suppress("UNCHECKED_CAST")
            override fun serialize(value: Any?): ByteArray = value
                ?.let { json.encodeToString(pageSerializer, it as List<ChatMessage>).toByteArray() }
                ?: ByteArray(0)

            override fun deserialize(bytes: ByteArray?): Any? = bytes
                ?.takeIf { it.isNotEmpty() }
                ?.let { json.decodeFromString(pageSerializer, it.decodeToString()) }
        }
    }

    private companion object {
        const val TTL_MINUTES = 10L
    }
}
