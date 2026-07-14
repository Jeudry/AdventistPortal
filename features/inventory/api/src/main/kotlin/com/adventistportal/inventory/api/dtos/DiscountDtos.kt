package com.adventistportal.inventory.api.dtos

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.domain.model.DiscountType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class DiscountDto(
    val id: DiscountId,
    val name: String,
    val description: String?,
    val type: DiscountType,
    val value: BigDecimal,
    val startDate: Instant?,
    val endDate: Instant?,
    val isActive: Boolean,
    val targetCategoryId: CategoryId?,
    val targetArticleId: ArticleId?,
    val targetVariantId: UUID?,
    val priority: Int
)

data class CreateDiscountInput(
    val name: String,
    val description: String?,
    val type: DiscountType,
    val value: BigDecimal,
    val startDate: Instant?,
    val endDate: Instant?,
    val targetCategoryId: CategoryId?,
    val targetArticleId: ArticleId?,
    val targetVariantId: UUID?,
    val priority: Int = 0
)