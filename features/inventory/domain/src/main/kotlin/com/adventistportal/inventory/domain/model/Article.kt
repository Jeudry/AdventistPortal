package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
import java.math.BigDecimal
import java.util.*

data class Article(
    val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String? = null,
    val isActive: Boolean = true,
    val categoryId: CategoryId? = null,
    val variants: List<ArticleVariant> = emptyList()
)

data class ArticleVariant(
    val id: UUID,
    val sku: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val stock: Int = 0,
    /** What it would cost to replace the item — for insurance and asset records, not a sale price. */
    val replacementCost: BigDecimal = BigDecimal.ZERO,
    val attributes: Map<String, String> = emptyMap(),
    val dimensions: List<ArticleDimensionDomain> = emptyList()
)

data class ArticleDimensionDomain(
    val label: String,
    val widthCm: Double? = null,
    val heightCm: Double? = null,
    val depthCm: Double? = null,
    val weightKg: Double? = null
)