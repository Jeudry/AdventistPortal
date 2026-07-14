package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.domain.model.Discount
import java.time.Instant

interface DiscountService {
    fun createDiscount(discount: Discount): DiscountId
    fun updateDiscount(discount: Discount): DiscountId
    fun deleteDiscount(id: DiscountId)
    fun getDiscount(id: DiscountId): Discount?
    fun getActiveDiscounts(now: Instant = Instant.now()): List<Discount>
}