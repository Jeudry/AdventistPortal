package com.rosafiesta.inventory.infra.db.mappers

import com.rosafiesta.inventory.domain.model.Category
import com.rosafiesta.inventory.infra.db.entities.CategoryEntity

fun CategoryEntity.toDomain(): Category = Category(
    id = this.id,
    name = this.name,
    description = this.description,
    iconName = this.iconName,
    isActive = this.isActive,
    parentId = this.parent?.id
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    iconName = this.iconName,
    isActive = this.isActive
)