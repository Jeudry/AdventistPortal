package com.rosafiesta.inventory.domain.repository

import com.rosafiesta.inventory.domain.model.ArticleFilterParams
import com.rosafiesta.inventory.domain.model.ArticleSummary

interface ArticleQueryRepository {
    fun findSummaries(filter: ArticleFilterParams): List<ArticleSummary>
}