package com.adventistportal.inventory.api.mappers

import com.adventistportal.inventory.api.dtos.CategoryDto
import com.adventistportal.inventory.api.dtos.CreateCategoryInput
import com.adventistportal.inventory.api.dtos.UpdateCategoryInput
import com.adventistportal.inventory.domain.model.Category
import java.util.UUID
import org.springframework.stereotype.Component

@Component
class CategoryMapper {

    fun toDto(category: Category): CategoryDto {
        return CategoryDto(
            id = category.id,
            name = category.name,
            description = category.description,
            iconName = category.iconName,
            isActive = category.isActive,
            parentId = category.parentId
        )
    }

    fun toDomain(input: CreateCategoryInput): Category {
        return Category(
            id = UUID.randomUUID(),
            name = input.name,
            description = input.description,
            iconName = input.iconName,
            isActive = input.isActive,
            parentId = input.parentId
        )
    }

    fun toDomain(input: UpdateCategoryInput): Category {
        return Category(
            id = input.id,
            name = input.name,
            description = input.description,
            iconName = input.iconName,
            isActive = input.isActive,
            parentId = input.parentId
        )
    }
}