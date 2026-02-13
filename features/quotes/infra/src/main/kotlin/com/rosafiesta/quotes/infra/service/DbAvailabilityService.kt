package com.rosafiesta.quotes.infra.service

import com.rosafiesta.quotes.domain.service.AvailabilityService
import com.rosafiesta.shared.domain.quotes.enums.QuoteStatus
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class DbAvailabilityService(
    private val entityManager: EntityManager
) : AvailabilityService {

    override fun getAvailableStock(variantId: UUID, startDate: Instant, endDate: Instant): Int {
        val totalStock = entityManager.createQuery(
            "SELECT v.stock FROM ArticleVariantEntity v WHERE v.id = :variantId",
            Int::class.java
        ).setParameter("variantId", variantId)
         .singleResult

        val committedStock = entityManager.createQuery(
            """
            SELECT COALESCE(SUM(i.quantity), 0) 
            FROM QuoteItemEntity i 
            JOIN i.quote q 
            WHERE i.variant.id = :variantId 
            AND q.status = :status
            AND (
                (q.eventStartDate <= :endDate AND q.eventEndDate >= :startDate)
            )
            """,
            Long::class.java
        ).setParameter("variantId", variantId)
         .setParameter("status", QuoteStatus.RESERVED)
         .setParameter("startDate", startDate)
         .setParameter("endDate", endDate)
         .singleResult.toInt()

        return totalStock - committedStock
    }
}