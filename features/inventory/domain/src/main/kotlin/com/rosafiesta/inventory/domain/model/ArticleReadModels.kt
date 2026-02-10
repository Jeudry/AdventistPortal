package com.rosafiesta.inventory.domain.model

import com.rosafiesta.core.domain.types.ArticleId
import java.time.Instant

/**
 * Domain Read Model for Article Summary (Lightweight).
 * This is NOT a DTO, but a projection of the domain data.
 */
data class ArticleSummary(
    val id: ArticleId,
    val nameTemplate: String,
    val descriptionTemplate: String?,
    val createdAt: Instant?
)

/**
 * Domain Filter Parameters.
 */
data class ArticleFilterParams(
    val id: ArticleId? = null,
    val title: String? = null,
    val authorId: String? = null
)