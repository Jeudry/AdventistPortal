package com.adventistportal.inventory.api.controllers

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.api.dtos.*
import com.adventistportal.inventory.api.mappers.ArticleMapper
import com.adventistportal.inventory.domain.model.ArticleFilterParams
import com.adventistportal.inventory.domain.repository.ArticleQueryRepository
import com.adventistportal.inventory.service.ArticleService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
@PreAuthorize("isAuthenticated()")
class ArticleGraphQLController(
    private val articleService: ArticleService,
    private val articleQueryRepository: ArticleQueryRepository,
    private val articleMapper: ArticleMapper
) {

    @QueryMapping
    fun articles(
        @Argument id: ArticleId?,
        @Argument title: String?,
        @Argument authorId: String?
    ): List<ArticleSummaryDto> {
        val filter = ArticleFilterParams(id, title, authorId)
        val summaries = articleQueryRepository.findSummaries(filter)
        
        return summaries.map { 
            ArticleSummaryDto(it.id, it.nameTemplate, it.descriptionTemplate, it.createdAt)
        }
    }

    @QueryMapping
    fun article(@Argument id: ArticleId): ArticleDto? {
        val article = articleService.getArticle(id) ?: return null
        return articleMapper.toDto(article)
    }

    @MutationMapping
    fun createArticle(@Argument input: CreateArticleInput): ArticleId {
        return articleService.addArticle(articleMapper.toDomain(input))
    }

    @MutationMapping
    fun updateArticle(@Argument input: UpdateArticleInput): ArticleId {
        return articleService.updateArticle(input.id, articleMapper.toDomain(input))
    }

    @MutationMapping
    fun deleteArticle(@Argument id: ArticleId) {
        articleService.deleteArticle(id)
    }
}