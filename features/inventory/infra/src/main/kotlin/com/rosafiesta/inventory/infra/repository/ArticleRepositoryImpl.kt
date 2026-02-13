package com.rosafiesta.inventory.infra.repository

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.inventory.domain.model.*
import com.rosafiesta.inventory.domain.repository.ArticleQueryRepository
import com.rosafiesta.inventory.domain.repository.ArticleRepository
import com.rosafiesta.inventory.infra.db.entities.ArticleEntity
import com.rosafiesta.inventory.infra.db.entities.ArticleVariantEntity
import com.rosafiesta.inventory.infra.db.mappers.fromDomain
import com.rosafiesta.inventory.infra.db.mappers.toDomain
import com.rosafiesta.inventory.infra.db.projections.ArticleSummaryProjection
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.FluentQuery
import org.springframework.stereotype.Repository
import java.util.*

/**
 * JPA implementation of ArticleRepository.
 */
@Repository
class ArticleRepositoryImpl(
    private val jpaRepository: ArticleJpaRepository,
    private val variantJpaRepository: ArticleVariantJpaRepository
) : ArticleRepository, ArticleQueryRepository {

    override fun findById(id: ArticleId): Article? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findVariantById(id: UUID): ArticleVariant? {
        return variantJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun save(article: Article): Article {
        val entity = article.fromDomain()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun deleteById(id: ArticleId) {
        jpaRepository.deleteById(id)
    }
    
    override fun retrieveAll(): List<Article> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findSummaries(filter: ArticleFilterParams): List<ArticleSummary> {
        val spec = Specification<ArticleEntity> { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()
            
            filter.id?.let { predicates.add(cb.equal(root.get<ArticleId>("id"), it)) }
            filter.title?.let { predicates.add(cb.like(cb.lower(root.get("nameTemplate")), "%${it.lowercase()}%")) }
            
            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }
        
        return jpaRepository.findBy(spec) { query: FluentQuery.FetchableFluentQuery<ArticleEntity> -> 
            query.`as`(ArticleSummaryProjection::class.java).all()
        }.map { 
             ArticleSummary(it.getId(), it.getNameTemplate(), it.getDescriptionTemplate(), it.getCreatedAt())
        }
    }
}

/**
 * JPA repository interface for ArticleEntity.
 */
interface ArticleJpaRepository : JpaRepository<ArticleEntity, ArticleId>, JpaSpecificationExecutor<ArticleEntity>

/**
 * JPA repository interface for ArticleVariantEntity.
 */
interface ArticleVariantJpaRepository : JpaRepository<ArticleVariantEntity, UUID>