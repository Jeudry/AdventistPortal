package com.rosafiesta.inventory.service

import com.rosafiesta.core.domain.types.DiscountId
import com.rosafiesta.inventory.domain.model.Discount
import java.time.Instant

interface DiscountService {
    fun createDiscount(discount: Discount): DiscountId
    fun updateDiscount(discount: Discount): DiscountId
    fun deleteDiscount(id: DiscountId)
    fun getDiscount(id: DiscountId): Discount?
    fun getActiveDiscounts(now: Instant = Instant.now()): List<Discount>
}