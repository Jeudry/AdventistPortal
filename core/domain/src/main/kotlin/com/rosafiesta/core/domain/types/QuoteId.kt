package com.rosafiesta.core.domain.types

import java.util.*

@JvmInline
value class QuoteId(val value: UUID) {
    companion object {
        fun generate() = QuoteId(UUID.randomUUID())
        fun fromString(value: String) = QuoteId(UUID.fromString(value))
    }
}