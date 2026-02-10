package com.rosafiesta.inventory.service

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.inventory.domain.model.*
import com.rosafiesta.inventory.domain.repository.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository
): ArticleService {
    
    override fun addArticle(articleParams: ArticleParams): ArticleId {
        val article = articleParams.toDomain()
        return articleRepository.save(article).id
    }

    override fun updateArticle(id: ArticleId, articleParams: ArticleParams): ArticleId {
        val existing = articleRepository.findById(id) ?: throw NoSuchElementException("Article not found")
        val updated = articleParams.toDomain(id)
        articleRepository.save(updated)
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