package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.core.domain.types.DiscountId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Discount(
    val id: DiscountId,
    val name: String,
    val description: String? = null,
    val type: DiscountType,
    val value: BigDecimal,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val isActive: Boolean = true,
    val targetCategoryId: CategoryId? = null,
    val targetArticleId: ArticleId? = null,
    val targetVariantId: UUID? = null,
    val priority: Int = 0
) {
    fun isValidAt(now: Instant): Boolean {
        if (!isActive) return false
        if (startDate != null && now.isBefore(startDate)) return false
        if (endDate != null && now.isAfter(endDate)) return false
        return true
    }
}

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT
}