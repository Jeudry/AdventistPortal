package com.rosafiesta.inventory.domain.repository

import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.inventory.domain.model.Category

interface CategoryRepository {
    fun findById(id: CategoryId): Category?
    fun findAll(): List<Category>
    fun findByParentId(parentId: CategoryId): List<Category>
    fun save(category: Category): Category
    fun deleteById(id: CategoryId)
}