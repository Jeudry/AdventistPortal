package com.adventistportal.inventory.infra.db.mappers

import com.adventistportal.inventory.domain.model.*
import com.adventistportal.inventory.infra.db.embeded.ArticleDimensions
import com.adventistportal.inventory.infra.db.entities.ArticleEntity
import com.adventistportal.inventory.infra.db.entities.ArticleVariantEntity

fun Article.fromDomain(): ArticleEntity {
    val articleEntity = ArticleEntity(
        id = this.id,
        nameTemplate = this.nameTemplate,
        descriptionTemplate = this.descriptionTemplate,
        isActive = this.isActive,
        type = this.type
    )
    articleEntity.variants = this.variants.map { it.fromDomain(articleEntity) }.toMutableList()
    return articleEntity
}

fun ArticleVariant.fromDomain(articleEntity: ArticleEntity): ArticleVariantEntity {
    return ArticleVariantEntity(
        id = this.id,
        sku = this.sku,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        isActive = this.isActive,
        stock = this.stock,
        rentalPrice = this.rentalPrice,
        salePrice = this.salePrice,
        replacementCost = this.replacementCost,
        article = articleEntity,
        attributes = this.attributes.toMutableMap(),
        dimensions = this.dimensions.map { it.fromDomain() }.toMutableList()
    )
}

fun ArticleDimensionDomain.fromDomain(): ArticleDimensions {
    return ArticleDimensions(
        label = this.label,
        widthCm = this.widthCm,
        heightCm = this.heightCm,
        depthCm = this.depthCm,
        weightKg = this.weightKg
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = this.id ?: throw IllegalStateException("Article ID cannot be null"),
        nameTemplate = this.nameTemplate,
        descriptionTemplate = this.descriptionTemplate,
        isActive = this.isActive,
        type = this.type,
        categoryId = this.category?.id,
        variants = this.variants.map { it.toDomain() }
    )
}

fun ArticleVariantEntity.toDomain(): ArticleVariant {
    return ArticleVariant(
        id = this.id ?: java.util.UUID.randomUUID(),
        sku = this.sku,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        isActive = this.isActive,
        stock = this.stock,
        rentalPrice = this.rentalPrice,
        salePrice = this.salePrice,
        replacementCost = this.replacementCost,
        attributes = this.attributes.toMap(),
        dimensions = this.dimensions.map { it.toDomain() }
    )
}

fun ArticleDimensions.toDomain(): ArticleDimensionDomain {
    return ArticleDimensionDomain(
        label = this.label,
        widthCm = this.widthCm,
        heightCm = this.heightCm,
        depthCm = this.depthCm,
        weightKg = this.weightKg
    )
}