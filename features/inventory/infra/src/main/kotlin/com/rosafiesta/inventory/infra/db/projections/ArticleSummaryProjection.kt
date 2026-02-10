package com.rosafiesta.inventory.infra.db.projections

import com.rosafiesta.core.domain.types.ArticleId
import java.time.Instant

interface ArticleSummaryProjection {
    fun getId(): ArticleId
    fun getNameTemplate(): String
    fun getDescriptionTemplate(): String?
    fun getCreatedAt(): Instant?
}