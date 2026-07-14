package com.adventistportal.inventory.api.mappers

import com.adventistportal.inventory.api.dtos.CategoryDto
import com.adventistportal.inventory.api.dtos.CreateCategoryInput
import com.adventistportal.inventory.api.dtos.UpdateCategoryInput
import com.adventistportal.inventory.domain.model.Category
import com.adventistportal.inventory.domain.model.CategoryParams
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

    fun toParams(input: CreateCategoryInput): CategoryParams {
        return CategoryParams(
            name = input.name,
            description = input.description,
            iconName = input.iconName,
            isActive = input.isActive,
            parentId = input.parentId
        )
    }

    fun toParams(input: UpdateCategoryInput): CategoryParams {
        return CategoryParams(
            name = input.name,
            description = input.description,
            iconName = input.iconName,
            isActive = input.isActive,
            parentId = input.parentId
        )
    }
}