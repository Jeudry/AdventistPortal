package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.inventory.domain.model.Category
import com.adventistportal.inventory.domain.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    override fun createCategory(category: Category): CategoryId {
        return requireNotNull(categoryRepository.save(category).id) { "saving assigns the id" }
    }

    override fun updateCategory(id: CategoryId, category: Category): CategoryId {
        val existing = categoryRepository.findById(id) ?: throw NoSuchElementException("Category not found")
        return requireNotNull(categoryRepository.save(category.copy(id = id)).id) { "saving keeps the id" }
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