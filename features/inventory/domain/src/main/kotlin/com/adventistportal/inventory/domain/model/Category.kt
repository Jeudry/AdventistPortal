package com.adventistportal.inventory.domain.model

import com.adventistportal.core.domain.types.CategoryId

data class Category(
    /** Null until it is saved: the database assigns it, as it does for an article. */
    val id: CategoryId? = null,
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