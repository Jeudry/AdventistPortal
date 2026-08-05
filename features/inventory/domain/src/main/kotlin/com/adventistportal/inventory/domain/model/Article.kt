package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
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
    /** Replacement value in cents, for insurance and asset records. Cents, not a decimal:
     *  integers cannot drift the way floating point does, and the unit is in the name. */
    val replacementCostCents: Long = 0,
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