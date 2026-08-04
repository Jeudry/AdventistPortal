package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.inventory.domain.model.Category

interface CategoryService {
    fun createCategory(category: Category): CategoryId
    fun updateCategory(id: CategoryId, category: Category): CategoryId
    fun deleteCategory(id: CategoryId)
    fun getCategory(id: CategoryId): Category?
    fun getAllCategories(): List<Category>
    fun getChildren(parentId: CategoryId): List<Category>
}