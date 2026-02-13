package com.rosafiesta.quotes.service

import com.rosafiesta.core.domain.types.QuoteId
import com.rosafiesta.core.domain.types.UserId
import com.rosafiesta.inventory.domain.repository.ArticleRepository
import com.rosafiesta.quotes.domain.exceptions.*
import com.rosafiesta.quotes.domain.model.Quote
import com.rosafiesta.quotes.domain.model.QuoteItem
import com.rosafiesta.quotes.domain.repository.QuoteRepository
import com.rosafiesta.quotes.domain.service.AvailabilityService
import com.rosafiesta.shared.domain.quotes.enums.QuoteStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class QuoteService(
    private val quoteRepository: QuoteRepository,
    private val availabilityService: AvailabilityService,
    private val articleRepository: ArticleRepository
) {

    @Transactional
    fun addItemToQuote(
        clientId: UserId,
        variantId: UUID,
        quantity: Int,
        startDate: Instant,
        endDate: Instant
    ): Quote {
        val available = availabilityService.getAvailableStock(variantId, startDate, endDate)
        if (available < quantity) {
            throw InsufficientStockException(variantId, quantity, available)
        }

        val quote = quoteRepository.findActiveQuoteByClientId(clientId) 
            ?: createNewDraft(clientId, startDate, endDate)

        val variant = articleRepository.findVariantById(variantId)
            ?: throw IllegalArgumentException("Variant with ID $variantId not found")
        
        val updatedItems = quote.items.toMutableList()
        val existingItem = updatedItems.find { it.variantId == variantId }
        
        if (existingItem != null) {
            updatedItems.remove(existingItem)
            updatedItems.add(existingItem.copy(quantity = existingItem.quantity + quantity))
        } else {
            updatedItems.add(QuoteItem(
                variantId = variantId,
                quantity = quantity,
                unitPrice = variant.rentalPrice
            ))
        }

        return quoteRepository.save(quote.copy(items = updatedItems))
    }

    @Transactional
    fun confirmQuote(quoteId: QuoteId): Quote {
        val quote = quoteRepository.findById(quoteId) ?: throw QuoteNotFoundException(quoteId)
        
        if (quote.status != QuoteStatus.DRAFT && quote.status != QuoteStatus.PENDING_REVIEW) {
            throw InvalidQuoteStatusException(quote.status.name, QuoteStatus.RESERVED.name)
        }

        quote.items.forEach { item ->
            val available = availabilityService.getAvailableStock(item.variantId, quote.eventStartDate, quote.eventEndDate)
            if (available < item.quantity) {
                throw InsufficientStockException(item.variantId, item.quantity, available)
            }
        }

        return quoteRepository.save(quote.copy(status = QuoteStatus.RESERVED))
    }

    private fun createNewDraft(clientId: UserId, start: Instant, end: Instant): Quote {
        return Quote(
            id = QuoteId.generate(),
            clientId = clientId,
            status = QuoteStatus.DRAFT,
            eventStartDate = start,
            eventEndDate = end
        )
    }
}