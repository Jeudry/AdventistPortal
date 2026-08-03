package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.domain.model.Discount
import com.adventistportal.inventory.domain.repository.DiscountRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DiscountServiceImpl(
    private val discountRepository: DiscountRepository
) : DiscountService {

    override fun createDiscount(discount: Discount): DiscountId {
        return discountRepository.save(discount)
    }

    override fun updateDiscount(discount: Discount): DiscountId {
        return discountRepository.save(discount)
    }

    override fun deleteDiscount(id: DiscountId) {
        discountRepository.delete(id)
    }

    override fun getDiscount(id: DiscountId): Discount? {
        return discountRepository.findById(id)
    }

    override fun getActiveDiscounts(now: Instant): List<Discount> {
        return discountRepository.findAllActive(now)
    }
}