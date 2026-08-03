package com.adventistportal.inventory.domain.repository

import com.adventistportal.inventory.domain.model.ArticleFilterParams
import com.adventistportal.inventory.domain.model.ArticleSummary

interface ArticleQueryRepository {
    fun findSummaries(filter: ArticleFilterParams): List<ArticleSummary>
}