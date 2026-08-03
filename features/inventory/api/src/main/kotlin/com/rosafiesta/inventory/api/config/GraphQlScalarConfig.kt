package com.rosafiesta.inventory.api.config

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.math.BigDecimal
import java.time.Instant
import java.util.Locale
import java.util.UUID

/**
 * Backs the `scalar BigDecimal`, `scalar Instant` and `scalar UUID` declarations in
 * schema.graphqls. graphql-java resolves scalars by name and refuses to build the
 * schema without an implementation, so leaving these unregistered fails startup with
 * "There is no scalar implementation for the named '<x>' scalar type".
 */
@Configuration
class GraphQlScalarConfig {

    @Bean
    fun scalarsConfigurer(): RuntimeWiringConfigurer = RuntimeWiringConfigurer { wiring ->
        wiring.scalar(bigDecimalScalar())
        wiring.scalar(instantScalar())
        wiring.scalar(uuidScalar())
    }

    private fun bigDecimalScalar(): GraphQLScalarType =
        scalar("BigDecimal", "A fixed-precision decimal, serialised as a string to avoid float rounding.") { input ->
            when (input) {
                is BigDecimal -> input
                is String -> BigDecimal(input)
                is Int -> BigDecimal(input)
                is Long -> BigDecimal(input)
                is Double -> BigDecimal.valueOf(input)
                else -> null
            }
        }

    private fun instantScalar(): GraphQLScalarType =
        scalar("Instant", "An instant in time, as an ISO-8601 string in UTC.") { input ->
            when (input) {
                is Instant -> input
                is String -> Instant.parse(input)
                else -> null
            }
        }

    private fun uuidScalar(): GraphQLScalarType =
        scalar("UUID", "A universally unique identifier, as its canonical 36-character string.") { input ->
            when (input) {
                is UUID -> input
                is String -> UUID.fromString(input)
                else -> null
            }
        }

    /**
     * Every scalar here shares one shape: parse from its own type or from a string, and
     * serialise back with toString(). Only the conversion differs, so it is the parameter.
     */
    private fun scalar(
        name: String,
        description: String,
        convert: (Any) -> Any?,
    ): GraphQLScalarType {
        val coercing = object : Coercing<Any, String> {
            override fun serialize(dataFetcherResult: Any, context: GraphQLContext, locale: Locale): String =
                convert(dataFetcherResult)?.toString()
                    ?: throw CoercingSerializeException("Cannot serialise ${dataFetcherResult::class.simpleName} as $name")

            override fun parseValue(input: Any, context: GraphQLContext, locale: Locale): Any =
                runCatching { convert(input) }.getOrNull()
                    ?: throw CoercingParseValueException("Cannot parse $input as $name")

            override fun parseLiteral(
                input: Value<*>,
                variables: CoercedVariables,
                context: GraphQLContext,
                locale: Locale,
            ): Any {
                if (input !is StringValue) throw CoercingParseLiteralException("$name must be given as a string literal")
                val literal = input.value ?: throw CoercingParseLiteralException("$name cannot be null")
                return runCatching { convert(literal) }.getOrNull()
                    ?: throw CoercingParseLiteralException("Cannot parse $literal as $name")
            }
        }
        return GraphQLScalarType.newScalar()
            .name(name)
            .description(description)
            .coercing(coercing)
            .build()
    }
}
