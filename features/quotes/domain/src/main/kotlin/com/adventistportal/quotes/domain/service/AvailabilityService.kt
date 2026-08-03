package com.adventistportal.quotes.domain.service

import java.time.Instant
import java.util.UUID

interface AvailabilityService {
    fun getAvailableStock(
        variantId: UUID, 
        startDate: Instant, 
        endDate: Instant
    ): Int
}