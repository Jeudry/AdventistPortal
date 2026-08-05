package com.adventistportal.inventory.api.dtos

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.core.domain.types.CategoryId
import java.time.Instant
import java.util.*

// DTOs for API Response (GraphQL/REST)
@Serializable
data class ArticleDto(
    @Contextual val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String?,
    val isActive: Boolean,
    @Contextual val categoryId: CategoryId?,
    val variants: List<ArticleVariantDto>
)

@Serializable
data class ArticleSummaryDto(
    @Contextual val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String?,
    @Contextual val createdAt: Instant?
)

@Serializable
data class ArticleVariantDto(
    @Contextual val id: UUID,
    val sku: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val stock: Int,
    val replacementCostCents: Long,
    /** A list, matching the schema. The domain keeps a map, where a key cannot repeat. */
    val attributes: List<AttributeDto>,
    val dimensions: List<ArticleDimensionDto>
)

@Serializable
data class AttributeDto(
    val key: String,
    val value: String
)

@Serializable
data class ArticleDimensionDto(
    val label: String,
    val widthCm: Double?,
    val heightCm: Double?,
    val depthCm: Double?,
    val weightKg: Double?
)

@Serializable
data class CreateArticleInput(
    val nameTemplate: String,
    val descriptionTemplate: String?,
    @Contextual val categoryId: CategoryId?,
    val variants: List<CreateArticleVariantInput>
)

@Serializable
data class CreateArticleVariantInput(
    val sku: String,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val stock: Int,
    val replacementCostCents: Long,
    /** Optional in the schema, so optional here: omitting it used to be a 500. */
    val attributes: List<AttributeInput>? = null,
    val dimensions: List<ArticleDimensionInput>? = null
)

@Serializable
data class AttributeInput(
    val key: String,
    val value: String
)

@Serializable
data class ArticleDimensionInput(
    val label: String,
    val widthCm: Double?,
    val heightCm: Double?,
    val depthCm: Double?,
    val weightKg: Double?
)

@Serializable
data class UpdateArticleInput(
    @Contextual val id: ArticleId,
    val nameTemplate: String?,
    val descriptionTemplate: String?,
    val isActive: Boolean?,
    @Contextual val categoryId: CategoryId?
)