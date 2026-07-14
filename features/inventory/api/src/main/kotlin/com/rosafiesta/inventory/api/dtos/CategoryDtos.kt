package com.adventistportal.inventory.api.dtos

import com.adventistportal.core.domain.types.CategoryId

data class CategoryDto(
    val id: CategoryId,
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean,
    val parentId: CategoryId?
)

data class CreateCategoryInput(
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean = true,
    val parentId: CategoryId?
)

data class UpdateCategoryInput(
    val id: CategoryId,
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean,
    val parentId: CategoryId?
)