package com.rosafiesta.inventory.domain.model

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.shared.domain.inventory.enums.ArticleType
import java.math.BigDecimal
import java.util.*

data class ArticleParams(
    val nameTemplate: String,
    val descriptionTemplate: String? = null,
    val type: ArticleType,
    val categoryId: CategoryId? = null,
    val variants: List<ArticleVariantParams>
) {
    fun toDomain(id: ArticleId = UUID.randomUUID()): Article {
        return Article(
            id = id,
            nameTemplate = nameTemplate,
            descriptionTemplate = descriptionTemplate,
            type = type,
            categoryId = categoryId,
            variants = variants.map { it.toDomain() }
        )
    }
}

data class ArticleVariantParams(
    val sku: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val stock: Int,
    val rentalPrice: BigDecimal,
    val salePrice: BigDecimal,
    val replacementCost: BigDecimal,
    val attributes: Map<String, String>,
    val dimensions: List<ArticleDimensionParams>
) {
    fun toDomain(id: UUID = UUID.randomUUID()): ArticleVariant {
        return ArticleVariant(
            id = id,
            sku = sku,
            name = name,
            description = description,
            imageUrl = imageUrl,
            stock = stock,
            rentalPrice = rentalPrice,
            salePrice = salePrice,
            replacementCost = replacementCost,
            attributes = attributes,
            dimensions = dimensions.map { it.toDomain() }
        )
    }
}

data class ArticleDimensionParams(
    val label: String,
    val widthCm: Double? = null,
    val heightCm: Double? = null,
    val depthCm: Double? = null,
    val weightKg: Double? = null
) {
    fun toDomain(): ArticleDimensionDomain {
        return ArticleDimensionDomain(
            label = label,
            widthCm = widthCm,
            heightCm = heightCm,
            depthCm = depthCm,
            weightKg = weightKg
        )
    }
}