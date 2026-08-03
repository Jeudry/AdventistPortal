package com.adventistportal.quotes.infra.repository

import com.adventistportal.core.domain.types.QuoteId
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.inventory.infra.db.entities.ArticleVariantEntity
import com.adventistportal.quotes.domain.model.Quote
import com.adventistportal.quotes.domain.repository.QuoteRepository
import com.adventistportal.quotes.infra.db.entities.QuoteEntity
import com.adventistportal.quotes.infra.db.mappers.toDomain
import com.adventistportal.quotes.infra.db.mappers.toEntity
import com.adventistportal.shared.domain.quotes.enums.QuoteStatus
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataQuoteRepository : JpaRepository<QuoteEntity, QuoteId> {
    fun findFirstByClientIdAndStatus(clientId: UserId, status: QuoteStatus): QuoteEntity?
    fun findByStatus(status: QuoteStatus): List<QuoteEntity>
}

@Repository
class JpaQuoteRepository(
    private val springDataRepo: SpringDataQuoteRepository,
    private val entityManager: EntityManager
) : QuoteRepository {

    override fun findById(id: QuoteId): Quote? = 
        springDataRepo.findById(id).map { it.toDomain() }.orElse(null)

    override fun findActiveQuoteByClientId(clientId: UserId): Quote? =
        springDataRepo.findFirstByClientIdAndStatus(clientId, QuoteStatus.DRAFT)?.toDomain()

    override fun save(quote: Quote): Quote {
        val entity = quote.toEntity()
        
        // Resolver las variantes reales para los items antes de guardar
        entity.items.forEachIndexed { index, itemEntity ->
            val domainItem = quote.items[index]
            itemEntity.variant = entityManager.getReference(ArticleVariantEntity::class.java, domainItem.variantId)
        }
        
        return springDataRepo.save(entity).toDomain()
    }

    override fun delete(id: QuoteId) = springDataRepo.deleteById(id)

    override fun findByStatus(status: QuoteStatus): List<Quote> =
        springDataRepo.findByStatus(status).map { it.toDomain() }
}