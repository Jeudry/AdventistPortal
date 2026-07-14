package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.CategoryId
import java.util.UUID

data class Category(
    val id: CategoryId = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val iconName: String? = null,
    val isActive: Boolean = true,
    val parentId: CategoryId? = null
) {
    init {
        require(name.isNotBlank()) { "Category name cannot be blank" }
    }
}