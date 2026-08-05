package com.adventistportal.inventory.api.config

import graphql.scalars.ExtendedScalars
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class GraphQlConfig {

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder ->
            wiringBuilder
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.UUID)
        }
    }
}