package com.rosafiesta.inventory.domain.repository

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.inventory.domain.model.Article

/**
 * Repository interface for Article domain operations.
 */
interface ArticleRepository {
    /**
     * Finds an article by its ID.
     * @param id The article ID.
     * @return The article if found, null otherwise.
     */
    fun findById(id: ArticleId): Article?

    /**
     * Saves an article.
     * @param article The article to save.
     * @return The saved article.
     */
    fun save(article: Article): Article

    /**
     * Deletes an article by its ID.
     * @param id The article ID.
     */
    fun deleteById(id: ArticleId)
    
    /**
     * Retrieves all articles.Retrieves 
     * @return A list of all articles.
     */
    fun retrieveAll(): List<Article>
}