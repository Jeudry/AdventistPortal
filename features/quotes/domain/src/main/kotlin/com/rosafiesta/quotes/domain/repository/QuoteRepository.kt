package com.rosafiesta.quotes.domain.repository

import com.rosafiesta.core.domain.types.QuoteId
import com.rosafiesta.core.domain.types.UserId
import com.rosafiesta.quotes.domain.model.Quote
import com.rosafiesta.shared.domain.quotes.enums.QuoteStatus

interface QuoteRepository {
    fun findById(id: QuoteId): Quote?
    fun findActiveQuoteByClientId(clientId: UserId): Quote?
    fun save(quote: Quote): Quote
    fun delete(id: QuoteId)
    fun findByStatus(status: QuoteStatus): List<Quote>
}