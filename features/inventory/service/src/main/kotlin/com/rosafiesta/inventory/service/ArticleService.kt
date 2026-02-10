package com.rosafiesta.inventory.service

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.inventory.domain.model.Article
import com.rosafiesta.inventory.domain.model.ArticleParams

/**
 * Service interface for managing articles.
 */
interface ArticleService {
  fun addArticle(articleParams: ArticleParams): ArticleId
  fun updateArticle(id: ArticleId, articleParams: ArticleParams): ArticleId
  fun deleteArticle(id: ArticleId)
  fun getArticle(id: ArticleId): Article?
  fun getAllArticles(): List<Article>
}