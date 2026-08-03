package com.adventistportal.inventory.api.mappers

import com.adventistportal.inventory.api.dtos.*
import com.adventistportal.inventory.domain.model.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class ArticleMapper {

    fun toDto(article: Article, activeDiscounts: List<Discount> = emptyList()): ArticleDto {
        return ArticleDto(
            id = article.id,
            nameTemplate = article.nameTemplate,
            descriptionTemplate = article.descriptionTemplate,
            isActive = article.isActive,
            type = article.type,
            categoryId = article.categoryId,
            variants = article.variants.map { toVariantDto(it, article, activeDiscounts) }
        )
    }

    private fun toVariantDto(
        variant: ArticleVariant,
        parentArticle: Article,
        activeDiscounts: List<Discount>
    ): ArticleVariantDto {
        val applicableDiscount = findBestDiscount(variant, parentArticle, activeDiscounts)
        
        val discountedRentalPrice = applicableDiscount?.let { calculateDiscountedPrice(variant.rentalPrice, it) }
        val discountedSalePrice = applicableDiscount?.let { calculateDiscountedPrice(variant.salePrice, it) }

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
            discountedRentalPrice = discountedRentalPrice,
            discountedSalePrice = discountedSalePrice,
            appliedDiscount = applicableDiscount?.toDto(),
            replacementCost = variant.replacementCost,
            attributes = variant.attributes,
            dimensions = variant.dimensions.map { toDimensionDto(it) }
        )
    }

    private fun findBestDiscount(
        variant: ArticleVariant,
        parentArticle: Article,
        activeDiscounts: List<Discount>
    ): Discount? {
        return activeDiscounts
            .filter { it.isActive }
            .filter { 
                it.targetVariantId == variant.id || 
                it.targetArticleId == parentArticle.id || 
                (it.targetCategoryId != null && it.targetCategoryId == parentArticle.categoryId)
            }
            .sortedWith(compareByDescending<Discount> { 
                // Priority: Variant > Article > Category
                when {
                    it.targetVariantId != null -> 3
                    it.targetArticleId != null -> 2
                    it.targetCategoryId != null -> 1
                    else -> 0
                }
            }.thenByDescending { it.priority })
            .firstOrNull()
    }

    private fun calculateDiscountedPrice(originalPrice: BigDecimal, discount: Discount): BigDecimal {
        return when (discount.type) {
            DiscountType.PERCENTAGE -> {
                val multiplier = BigDecimal.ONE.subtract(discount.value)
                originalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP)
            }
            DiscountType.FIXED_AMOUNT -> {
                originalPrice.subtract(discount.value).coerceAtLeast(BigDecimal.ZERO)
            }
        }
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
            type = com.adventistportal.shared.domain.inventory.enums.ArticleType.Rental,
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

    private fun Discount.toDto() = DiscountDto(
        id = id,
        name = name,
        description = description,
        type = type,
        value = value,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        targetCategoryId = targetCategoryId,
        targetArticleId = targetArticleId,
        targetVariantId = targetVariantId,
        priority = priority
    )
}