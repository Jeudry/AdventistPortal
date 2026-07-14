package com.adventistportal.inventory.api.controllers

import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.api.dtos.CreateDiscountInput
import com.adventistportal.inventory.api.dtos.DiscountDto
import com.adventistportal.inventory.domain.model.Discount
import com.adventistportal.inventory.service.DiscountService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.time.Instant
import java.util.*

@Controller
@PreAuthorize("isAuthenticated()")
class DiscountGraphQLController(
    private val discountService: DiscountService
) {

    @QueryMapping
    fun discounts(): List<DiscountDto> {
        return discountService.getActiveDiscounts().map { it.toDto() }
    }

    @QueryMapping
    fun discount(@Argument id: DiscountId): DiscountDto? {
        return discountService.getDiscount(id)?.toDto()
    }

    @MutationMapping
    fun createDiscount(@Argument input: CreateDiscountInput): DiscountId {
        val discount = Discount(
            id = UUID.randomUUID(),
            name = input.name,
            description = input.description,
            type = input.type,
            value = input.value,
            startDate = input.startDate,
            endDate = input.endDate,
            targetCategoryId = input.targetCategoryId,
            targetArticleId = input.targetArticleId,
            targetVariantId = input.targetVariantId,
            priority = input.priority
        )
        return discountService.createDiscount(discount)
    }

    @MutationMapping
    fun deleteDiscount(@Argument id: DiscountId) {
        discountService.deleteDiscount(id)
    }

    private fun Discount.toDto() = DiscountDto(
        id = id,
        name = name,
        description = description,
        type = type,
        value = value,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        targetCategoryId = targetCategoryId,
        targetArticleId = targetArticleId,
        targetVariantId = targetVariantId,
        priority = priority
    )
}