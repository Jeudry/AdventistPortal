package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.CategoryId

data class CategoryParams(
    val name: String,
    val description: String? = null,
    val iconName: String? = null,
    val isActive: Boolean = true,
    val parentId: CategoryId? = null
) {
    fun toDomain(id: CategoryId? = null): Category {
        return Category(
            id = id ?: java.util.UUID.randomUUID(),
            name = name,
            description = description,
            iconName = iconName,
            isActive = isActive,
            parentId = parentId
        )
    }
}