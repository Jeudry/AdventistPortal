package com.rosafiesta.inventory.api.controllers

import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.inventory.api.dtos.CategoryDto
import com.rosafiesta.inventory.api.dtos.CreateCategoryInput
import com.rosafiesta.inventory.api.dtos.UpdateCategoryInput
import com.rosafiesta.inventory.api.mappers.CategoryMapper
import com.rosafiesta.inventory.service.CategoryService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
@PreAuthorize("isAuthenticated()")
class CategoryGraphQLController(
    private val categoryService: CategoryService,
    private val categoryMapper: CategoryMapper
) {

    @QueryMapping
    fun categories(): List<CategoryDto> {
        return categoryService.getAllCategories().map { categoryMapper.toDto(it) }
    }

    @QueryMapping
    fun category(@Argument id: CategoryId): CategoryDto? {
        return categoryService.getCategory(id)?.let { categoryMapper.toDto(it) }
    }

    @SchemaMapping(typeName = "Category", field = "children")
    fun children(category: CategoryDto): List<CategoryDto> {
        return categoryService.getChildren(category.id).map { categoryMapper.toDto(it) }
    }

    @SchemaMapping(typeName = "Category", field = "parent")
    fun parent(category: CategoryDto): CategoryDto? {
        return category.parentId?.let { parentId ->
            categoryService.getCategory(parentId)?.let { categoryMapper.toDto(it) }
        }
    }

    @MutationMapping
    fun createCategory(@Argument input: CreateCategoryInput): CategoryId {
        return categoryService.createCategory(categoryMapper.toParams(input))
    }

    @MutationMapping
    fun updateCategory(@Argument input: UpdateCategoryInput): CategoryId {
        return categoryService.updateCategory(input.id, categoryMapper.toParams(input))
    }

    @MutationMapping
    fun deleteCategory(@Argument id: CategoryId) {
        categoryService.deleteCategory(id)
    }
}