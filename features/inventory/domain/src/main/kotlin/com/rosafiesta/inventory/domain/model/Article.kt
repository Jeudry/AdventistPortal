package com.rosafiesta.inventory.domain.model

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.shared.domain.inventory.enums.ArticleType
import java.math.BigDecimal
import java.util.*

data class Article(
    val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String? = null,
    val isActive: Boolean = true,
    val type: ArticleType = ArticleType.Rental,
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
    val rentalPrice: BigDecimal = BigDecimal.ZERO,
    val salePrice: BigDecimal = BigDecimal.ZERO,
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