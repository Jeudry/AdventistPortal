package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.domain.model.Article

/**
 * Service interface for managing articles.
 */
interface ArticleService {
  fun addArticle(article: Article): ArticleId
  fun updateArticle(id: ArticleId, article: Article): ArticleId
  fun deleteArticle(id: ArticleId)
  fun getArticle(id: ArticleId): Article?
  fun getAllArticles(): List<Article>
}