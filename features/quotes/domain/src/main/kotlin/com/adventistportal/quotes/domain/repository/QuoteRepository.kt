package com.adventistportal.quotes.domain.repository

import com.adventistportal.core.domain.types.QuoteId
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.quotes.domain.model.Quote
import com.adventistportal.shared.domain.quotes.enums.QuoteStatus

interface QuoteRepository {
    fun findById(id: QuoteId): Quote?
    fun findActiveQuoteByClientId(clientId: UserId): Quote?
    fun save(quote: Quote): Quote
    fun delete(id: QuoteId)
    fun findByStatus(status: QuoteStatus): List<Quote>
}