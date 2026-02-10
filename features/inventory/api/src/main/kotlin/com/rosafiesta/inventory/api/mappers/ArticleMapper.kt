package com.rosafiesta.inventory.api.mappers

import com.rosafiesta.inventory.api.dtos.*
import com.rosafiesta.inventory.domain.model.*
import org.springframework.stereotype.Component

@Component
class ArticleMapper {

    fun toDto(article: Article): ArticleDto {
        return ArticleDto(
            id = article.id,
            nameTemplate = article.nameTemplate,
            descriptionTemplate = article.descriptionTemplate,
            isActive = article.isActive,
            type = article.type,
            categoryId = article.categoryId,
            variants = article.variants.map { toVariantDto(it) }
        )
    }

    private fun toVariantDto(variant: ArticleVariant): ArticleVariantDto {
        return ArticleVariantDto(
            id = variant.id,
            sku = variant.sku,
            name = variant.name,
            description = variant.description,
            imageUrl = variant.imageUrl,
            isActive = variant.isActive,
            stock = variant.stock,
            rentalPrice = variant.rentalPrice,
            salePrice = variant.salePrice,
            replacementCost = variant.replacementCost,
            attributes = variant.attributes,
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

    fun toParams(input: CreateArticleInput): ArticleParams {
        return ArticleParams(
            nameTemplate = input.nameTemplate,
            descriptionTemplate = input.descriptionTemplate,
            type = input.type,
            categoryId = input.categoryId,
            variants = input.variants.map { toVariantParams(it) }
        )
    }

    fun toParams(input: UpdateArticleInput): ArticleParams {
        return ArticleParams(
            nameTemplate = input.nameTemplate ?: "",
            descriptionTemplate = input.descriptionTemplate,
            type = com.rosafiesta.shared.domain.inventory.enums.ArticleType.Rental, // Default or fetch existing
            categoryId = input.categoryId,
            variants = emptyList()
        )
    }

    private fun toVariantParams(input: CreateArticleVariantInput): ArticleVariantParams {
        return ArticleVariantParams(
            sku = input.sku,
            name = input.name,
            description = input.description,
            imageUrl = input.imageUrl,
            stock = input.stock,
            rentalPrice = input.rentalPrice,
            salePrice = input.salePrice,
            replacementCost = input.replacementCost,
            attributes = input.attributes,
            dimensions = input.dimensions.map { toDimensionParams(it) }
        )
    }

    private fun toDimensionParams(input: ArticleDimensionInput): ArticleDimensionParams {
        return ArticleDimensionParams(
            label = input.label,
            widthCm = input.widthCm,
            heightCm = input.heightCm,
            depthCm = input.depthCm,
            weightKg = input.weightKg
        )
    }
}