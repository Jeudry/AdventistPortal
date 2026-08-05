package com.adventistportal.inventory.api.mappers

import com.adventistportal.inventory.api.dtos.*
import com.adventistportal.inventory.domain.model.*
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ArticleMapper {

    fun toDto(article: Article): ArticleDto {
        return ArticleDto(
            id = requireNotNull(article.id) { "an article read back from storage always has an id" },
            nameTemplate = article.nameTemplate,
            descriptionTemplate = article.descriptionTemplate,
            isActive = article.isActive,
            categoryId = article.categoryId,
            variants = article.variants.map { toVariantDto(it) }
        )
    }

    private fun toVariantDto(variant: ArticleVariant): ArticleVariantDto {
        return ArticleVariantDto(
            id = requireNotNull(variant.id) { "a variant read back from storage always has an id" },
            sku = variant.sku,
            name = variant.name,
            description = variant.description,
            imageUrl = variant.imageUrl,
            isActive = variant.isActive,
            stock = variant.stock,
            replacementCostCents = variant.replacementCostCents,
            attributes = variant.attributes.map { (key, value) -> AttributeDto(key, value) },
            dimensions = variant.dimensions.map { toDimensionDto(it) }
        )
    }

    private fun toDimensionDto(dimension: ArticleDimensionDomain): ArticleDimensionDto {
        return ArticleDimensionDto(
            label = dimension.label,
            widthCm = dimension.widthCm,
            heightCm = dimension.heightCm,
            depthCm = dimension.depthCm,
            weightKg = dimension.weightKg
        )
    }

    fun toDomain(input: CreateArticleInput): Article {
        return Article(
            nameTemplate = input.nameTemplate,
            descriptionTemplate = input.descriptionTemplate,
            categoryId = input.categoryId,
            variants = input.variants.map { toVariant(it) }
        )
    }

    fun toDomain(input: UpdateArticleInput): Article {
        return Article(
            id = input.id,
            nameTemplate = input.nameTemplate ?: "",
            descriptionTemplate = input.descriptionTemplate,
            isActive = input.isActive ?: true,
            categoryId = input.categoryId,
            variants = emptyList()
        )
    }

    private fun toVariant(input: CreateArticleVariantInput): ArticleVariant {
        return ArticleVariant(
            sku = input.sku,
            name = input.name,
            description = input.description,
            imageUrl = input.imageUrl,
            stock = input.stock,
            replacementCostCents = input.replacementCostCents,
            attributes = input.attributes.orEmpty().associate { it.key to it.value },
            dimensions = input.dimensions.orEmpty().map { toDimension(it) }
        )
    }

    private fun toDimension(input: ArticleDimensionInput): ArticleDimensionDomain {
        return ArticleDimensionDomain(
            label = input.label,
            widthCm = input.widthCm,
            heightCm = input.heightCm,
            depthCm = input.depthCm,
            weightKg = input.weightKg
        )
    }
}
