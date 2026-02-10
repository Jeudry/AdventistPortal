package com.rosafiesta.inventory.service

import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.inventory.domain.model.Category
import com.rosafiesta.inventory.domain.model.CategoryParams
import com.rosafiesta.inventory.domain.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    override fun createCategory(params: CategoryParams): CategoryId {
        val category = params.toDomain()
        return categoryRepository.save(category).id
    }

    override fun updateCategory(id: CategoryId, params: CategoryParams): CategoryId {
        val existing = categoryRepository.findById(id) ?: throw NoSuchElementException("Category not found")
        val updated = params.toDomain(id)
        categoryRepository.save(updated)
        return updated.id
    }

    override fun deleteCategory(id: CategoryId) {
        categoryRepository.deleteById(id)
    }

    override fun getCategory(id: CategoryId): Category? {
        return categoryRepository.findById(id)
    }

    override fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    override fun getChildren(parentId: CategoryId): List<Category> {
        return categoryRepository.findByParentId(parentId)
    }
}