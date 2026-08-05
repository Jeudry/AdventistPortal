package com.adventistportal.inventory.infra.db.mappers

import com.adventistportal.inventory.domain.model.Category
import com.adventistportal.inventory.infra.db.entities.CategoryEntity

fun CategoryEntity.toDomain(): Category = Category(
    id = this.id,
    name = this.name,
    description = this.description,
    iconName = this.iconName,
    isActive = this.isActive,
    parentId = this.parent?.id
)

/**
 * The parent is deliberately not set here, and setting it is the repository's job: it
 * needs a reference to another row, which this function has no way to obtain.
 *
 * It used to be silently missing instead, so a category created under another was stored
 * with no parent at all and the hierarchy simply never existed.
 */
fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    iconName = this.iconName,
    isActive = this.isActive
)