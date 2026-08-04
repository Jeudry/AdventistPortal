package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.domain.model.*
import com.adventistportal.inventory.domain.repository.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository
): ArticleService {
    
    override fun addArticle(article: Article): ArticleId {
        return articleRepository.save(article).id
    }

    override fun updateArticle(id: ArticleId, article: Article): ArticleId {
        val existing = articleRepository.findById(id) ?: throw NoSuchElementException("Article not found")
        articleRepository.save(article.copy(id = id))
        return id
    }

    override fun deleteArticle(id: ArticleId) {
        articleRepository.deleteById(id)
    }

    override fun getArticle(id: ArticleId): Article? {
        return articleRepository.findById(id)
    }

    override fun getAllArticles(): List<Article> {
        return articleRepository.retrieveAll()
    }
}