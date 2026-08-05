package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.domain.model.*
import com.adventistportal.inventory.domain.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Every method is transactional because the mapping to the domain walks the lazy
 * collections. `open-in-view` is off, so the session ends with the method rather than
 * with the HTTP response — the request thread must not be what keeps a database
 * connection alive.
 */
@Service
@Transactional
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository
): ArticleService {
    
    override fun addArticle(article: Article): ArticleId {
        return requireNotNull(articleRepository.save(article).id) { "saving assigns the id" }
    }

    override fun updateArticle(id: ArticleId, article: Article): ArticleId {
        val existing = articleRepository.findById(id) ?: throw NoSuchElementException("Article not found")
        articleRepository.save(article.copy(id = id))
        return id
    }

    override fun deleteArticle(id: ArticleId) {
        articleRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getArticle(id: ArticleId): Article? {
        return articleRepository.findById(id)
    }

    @Transactional(readOnly = true)
    override fun getAllArticles(): List<Article> {
        return articleRepository.retrieveAll()
    }
}