package com.adventistportal.inventory.infra.repository

import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.inventory.domain.model.Category
import com.adventistportal.inventory.domain.repository.CategoryRepository
import com.adventistportal.inventory.infra.db.entities.CategoryEntity
import com.adventistportal.inventory.infra.db.mappers.toDomain
import com.adventistportal.inventory.infra.db.mappers.toEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository
) : CategoryRepository {

    override fun findById(id: CategoryId): Category? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Category> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByParentId(parentId: CategoryId): List<Category> {
        return jpaRepository.findByParentId(parentId).map { it.toDomain() }
    }

    override fun save(category: Category): Category {
        val entity = category.toEntity()
        return jpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: CategoryId) {
        jpaRepository.deleteById(id)
    }
}

interface CategoryJpaRepository : JpaRepository<CategoryEntity, CategoryId> {
    fun findByParentId(parentId: CategoryId): List<CategoryEntity>
}