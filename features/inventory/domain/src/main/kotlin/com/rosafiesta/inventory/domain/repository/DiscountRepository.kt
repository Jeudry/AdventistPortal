package com.rosafiesta.inventory.domain.repository

import com.rosafiesta.core.domain.types.DiscountId
import com.rosafiesta.inventory.domain.model.Discount
import java.time.Instant

interface DiscountRepository {
    fun findById(id: DiscountId): Discount?
    fun findAllActive(now: Instant): List<Discount>
    fun save(discount: Discount): DiscountId
    fun delete(id: DiscountId)
}