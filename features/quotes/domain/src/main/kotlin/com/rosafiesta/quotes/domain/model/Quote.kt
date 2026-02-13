package com.rosafiesta.quotes.domain.model

import com.rosafiesta.core.domain.types.QuoteId
import com.rosafiesta.core.domain.types.UserId
import com.rosafiesta.shared.domain.quotes.enums.QuoteStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Quote(
    val id: QuoteId,
    val clientId: UserId,
    val status: QuoteStatus = QuoteStatus.DRAFT,
    val eventStartDate: Instant,
    val eventEndDate: Instant,
    val venueAddress: String? = null,
    val deliveryZoneId: String? = null,
    val items: List<QuoteItem> = emptyList(),
    val createdAt: Instant = Instant.now()
) {
    fun getTotalAmount(): BigDecimal = items.fold(BigDecimal.ZERO) { acc, item -> 
        acc.add(item.getTotalPrice()) 
    }
}

data class QuoteItem(
    val id: UUID = UUID.randomUUID(),
    val variantId: UUID,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val addedAt: Instant = Instant.now()
) {
    fun getTotalPrice(): BigDecimal = unitPrice.multiply(BigDecimal(quantity))
}