package com.adventistportal.inventory.api.dtos

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

// DTOs for API Response (GraphQL/REST)
data class ArticleDto(
    val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String?,
    val isActive: Boolean,
    val categoryId: CategoryId?,
    val variants: List<ArticleVariantDto>
)

data class ArticleSummaryDto(
    val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String?,
    val createdAt: Instant?
)

data class ArticleVariantDto(
    val id: UUID,
    val sku: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val stock: Int,
    val replacementCost: BigDecimal,
    val attributes: Map<String, String>,
    val dimensions: List<ArticleDimensionDto>
)

data class ArticleDimensionDto(
    val label: String,
    val widthCm: Double?,
    val heightCm: Double?,
    val depthCm: Double?,
    val weightKg: Double?
)

data class CreateArticleInput(
    val nameTemplate: String,
    val descriptionTemplate: String?,
    val categoryId: CategoryId?,
    val variants: List<CreateArticleVariantInput>
)

data class CreateArticleVariantInput(
    val sku: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val stock: Int,
    val replacementCost: BigDecimal,
    val attributes: Map<String, String>,
    val dimensions: List<ArticleDimensionInput>
)

data class ArticleDimensionInput(
    val label: String,
    val widthCm: Double?,
    val heightCm: Double?,
    val depthCm: Double?,
    val weightKg: Double?
)

data class UpdateArticleInput(
    val id: ArticleId,
    val nameTemplate: String?,
    val descriptionTemplate: String?,
    val isActive: Boolean?,
    val categoryId: CategoryId?
)