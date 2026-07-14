package com.adventistportal.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper

@Configuration
class Jackson2Config {

    @Bean("jackson2ObjectMapper")
    fun jackson2ObjectMapper(): JsonMapper {
        // Jackson 3: immutable builder; Kotlin + java.time modules are
        // auto-registered from the classpath via findAndAddModules().
        return JsonMapper.builder()
            .findAndAddModules()
            .build()
    }
}