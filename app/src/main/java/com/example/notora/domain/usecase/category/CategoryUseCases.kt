package com.example.notora.domain.usecase.category

import com.example.notora.common.util.AppUtils
import com.example.notora.domain.model.Category
import com.example.notora.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all note categories.
 */
class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}

/**
 * Use case to validate and persist a category.
 */
class SaveCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long, name: String, colorHex: String): Boolean {
        val trimmed = name.trim()
        if (!AppUtils.isValidTitle(trimmed)) return false
        val validColor = if (AppUtils.isValidHexColor(colorHex)) colorHex else "#6750A4"

        categoryRepository.saveCategory(
            Category(id = id, name = trimmed, colorHex = validColor)
        )
        return true
    }
}

/**
 * Use case to delete a category by its ID.
 */
class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) {
        categoryRepository.deleteCategory(id)
    }
}
