package com.adventistportal.inventory.api.dtos

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.CategoryId

@Serializable
data class CategoryDto(
    @Contextual val id: CategoryId,
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean,
    @Contextual val parentId: CategoryId?
)

@Serializable
data class CreateCategoryInput(
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean = true,
    @Contextual val parentId: CategoryId?
)

@Serializable
data class UpdateCategoryInput(
    @Contextual val id: CategoryId,
    val name: String,
    val description: String?,
    val iconName: String?,
    val isActive: Boolean,
    @Contextual val parentId: CategoryId?
)