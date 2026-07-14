package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.domain.model.Article
import com.adventistportal.inventory.domain.model.ArticleParams

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