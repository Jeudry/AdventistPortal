package com.adventistportal.inventory.service

import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.inventory.domain.model.Category
import com.adventistportal.inventory.domain.model.CategoryParams

interface CategoryService {
    fun createCategory(params: CategoryParams): CategoryId
    fun updateCategory(id: CategoryId, params: CategoryParams): CategoryId
    fun deleteCategory(id: CategoryId)
    fun getCategory(id: CategoryId): Category?
    fun getAllCategories(): List<Category>
    fun getChildren(parentId: CategoryId): List<Category>
}