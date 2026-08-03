package com.adventistportal.inventory.infra.repository

import com.adventistportal.core.domain.types.DiscountId
import com.adventistportal.inventory.domain.model.Discount
import com.adventistportal.inventory.domain.model.DiscountType
import com.adventistportal.inventory.domain.repository.DiscountRepository
import com.adventistportal.inventory.infra.db.entities.DiscountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class DiscountRepositoryImpl(
    private val jpaRepository: DiscountJpaRepository
) : DiscountRepository {

    override fun findById(id: DiscountId): Discount? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAllActive(now: Instant): List<Discount> {
        return jpaRepository.findAllActive(now).map { it.toDomain() }
    }

    override fun save(discount: Discount): DiscountId {
        val entity = discount.toEntity()
        return jpaRepository.save(entity).id!!
    }

    override fun delete(id: DiscountId) {
        jpaRepository.deleteById(id)
    }

    private fun DiscountEntity.toDomain() = Discount(
        id = id!!,
        name = name,
        description = description,
        type = type,
        value = value,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        targetCategoryId = targetCategoryId,
        targetArticleId = targetArticleId,
        targetVariantId = targetVariantId,
        priority = priority
    )

    private fun Discount.toEntity() = DiscountEntity(
        id = id,
        name = name,
        description = description,
        type = type,
        value = value,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        targetCategoryId = targetCategoryId,
        targetArticleId = targetArticleId,
        targetVariantId = targetVariantId,
        priority = priority
    )
}

interface DiscountJpaRepository : JpaRepository<DiscountEntity, DiscountId> {
    @Query("SELECT d FROM DiscountEntity d WHERE d.isActive = true AND (d.startDate IS NULL OR d.startDate <= :now) AND (d.endDate IS NULL OR d.endDate >= :now)")
    fun findAllActive(now: Instant): List<DiscountEntity>
}