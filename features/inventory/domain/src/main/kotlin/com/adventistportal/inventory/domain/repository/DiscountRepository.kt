package com.adventistportal.inventory.domain.repository

import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.domain.model.Discount
import java.time.Instant

interface DiscountRepository {
    fun findById(id: DiscountId): Discount?
    fun findAllActive(now: Instant): List<Discount>
    fun save(discount: Discount): DiscountId
    fun delete(id: DiscountId)
}