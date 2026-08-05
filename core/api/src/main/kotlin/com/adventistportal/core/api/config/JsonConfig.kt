package com.adventistportal.core.api.config

import com.adventistportal.core.api.serialization.apiSerializersModule
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverters
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Serialises the API with kotlinx.serialization instead of Jackson, so request and
 * response models can move to `shared` and be used by the KMP client unchanged —
 * Jackson has no Kotlin/Native implementation and cannot cross.
 */
@Configuration
class JsonConfig : WebMvcConfigurer {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        serializersModule = apiSerializersModule
    }

    override fun configureMessageConverters(builder: HttpMessageConverters.ServerBuilder) {
        builder.withKotlinSerializationJsonConverter(KotlinSerializationJsonHttpMessageConverter(json))
    }
}
