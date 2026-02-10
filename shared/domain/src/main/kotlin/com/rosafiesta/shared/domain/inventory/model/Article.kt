package com.rosafiesta.shared.domain.inventory.model

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.shared.domain.inventory.validation.ArticleValidationUtils

/// <summary>Represents an article in the inventory domain.</summary>
data class Article(
    val id: ArticleId,
    val name: String,
    val code: String,
) {
    init {
        require(ArticleValidationUtils.isNameValid(name)) { "Article name cannot be blank" }
        require(ArticleValidationUtils.isCodeValid(code)) { "Invalid article code: must be ${ArticleValidationUtils.CODE_MIN_LENGTH}-${ArticleValidationUtils.CODE_MAX_LENGTH} alphanumeric characters" }
    }
}