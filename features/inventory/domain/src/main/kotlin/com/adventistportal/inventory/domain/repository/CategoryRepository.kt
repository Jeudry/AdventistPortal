package com.adventistportal.inventory.domain.repository

import com.adventistportal.core.domain.types.CategoryId
import com.adventistportal.inventory.domain.model.Category

interface CategoryRepository {
    fun findById(id: CategoryId): Category?
    fun findAll(): List<Category>
    fun findByParentId(parentId: CategoryId): List<Category>
    fun save(category: Category): Category
    fun deleteById(id: CategoryId)
}