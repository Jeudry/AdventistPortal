package com.rosafiesta.quotes.api.controllers

import com.rosafiesta.core.domain.types.QuoteId
import com.rosafiesta.core.domain.types.UserId
import com.rosafiesta.quotes.domain.model.Quote
import com.rosafiesta.quotes.service.QuoteService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.time.Instant
import java.util.*

@Controller
class QuoteGraphQLController(
    private val quoteService: QuoteService
) {

    @MutationMapping
    fun addVariantToQuote(
        @Argument variantId: String,
        @Argument quantity: Int,
        @Argument startDate: String,
        @Argument endDate: String
    ): Quote {
        val mockClientId: UserId = UUID.fromString("00000000-0000-0000-0000-000000000001") 
        
        return quoteService.addItemToQuote(
            clientId = mockClientId,
            variantId = UUID.fromString(variantId),
            quantity = quantity,
            startDate = Instant.parse(startDate),
            endDate = Instant.parse(endDate)
        )
    }

    @MutationMapping
    fun confirmQuote(@Argument quoteId: String): Quote {
        return quoteService.confirmQuote(QuoteId(UUID.fromString(quoteId)))
    }

    @QueryMapping
    fun myActiveQuote(): Quote? {
        // Implementación pendiente de Auth
        return null 
    }
}