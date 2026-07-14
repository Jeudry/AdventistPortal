package com.adventistportal.quotes.domain.exceptions

import com.adventistportal.core.domain.types.QuoteId
import java.util.*

sealed class QuoteException(message: String) : RuntimeException(message)

class QuoteNotFoundException(id: QuoteId) : QuoteException("Quote with ID ${id.value} not found")

class InsufficientStockException(variantId: UUID, requested: Int, available: Int) : 
    QuoteException("Insufficient stock for variant $variantId. Requested: $requested, Available: $available")

class InvalidQuoteStatusException(current: String, target: String) : 
    QuoteException("Cannot transition quote from $current to $target")