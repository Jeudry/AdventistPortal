package com.adventistportal.inventory.infra.db.projections

import com.adventistportal.core.domain.types.ArticleId
import java.time.Instant

interface ArticleSummaryProjection {
    fun getId(): ArticleId
    fun getNameTemplate(): String
    fun getDescriptionTemplate(): String?
    fun getCreatedAt(): Instant?
}