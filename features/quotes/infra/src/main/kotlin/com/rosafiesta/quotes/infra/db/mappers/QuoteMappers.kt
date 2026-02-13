package com.rosafiesta.quotes.infra.db.mappers

import com.rosafiesta.quotes.domain.model.Quote
import com.rosafiesta.quotes.domain.model.QuoteItem
import com.rosafiesta.quotes.infra.db.entities.QuoteEntity
import com.rosafiesta.quotes.infra.db.entities.QuoteItemEntity
import com.rosafiesta.inventory.infra.db.entities.ArticleVariantEntity

fun Quote.toEntity(): QuoteEntity {
    val entity = QuoteEntity(
        id = this.id,
        clientId = this.clientId,
        status = this.status,
        eventStartDate = this.eventStartDate,
        eventEndDate = this.eventEndDate,
        venueAddress = this.venueAddress,
        createdAt = this.createdAt
    )
    entity.items = this.items.map { it.toEntity(entity) }.toMutableList()
    return entity
}

fun QuoteItem.toEntity(quoteEntity: QuoteEntity): QuoteItemEntity {
    return QuoteItemEntity(
        id = this.id,
        quote = quoteEntity,
        variant = null, // Se debe resolver en el repositorio/servicio
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        addedAt = this.addedAt
    )
}

fun QuoteEntity.toDomain(): Quote {
    return Quote(
        id = this.id ?: throw IllegalStateException("Quote ID cannot be null"),
        clientId = this.clientId,
        status = this.status,
        eventStartDate = this.eventStartDate,
        eventEndDate = this.eventEndDate,
        venueAddress = this.venueAddress,
        items = this.items.map { it.toDomain() },
        createdAt = this.createdAt ?: java.time.Instant.now()
    )
}

fun QuoteItemEntity.toDomain(): QuoteItem {
    return QuoteItem(
        id = this.id ?: java.util.UUID.randomUUID(),
        variantId = this.variant?.id ?: throw IllegalStateException("Variant ID cannot be null"),
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        addedAt = this.addedAt ?: java.time.Instant.now()
    )
}