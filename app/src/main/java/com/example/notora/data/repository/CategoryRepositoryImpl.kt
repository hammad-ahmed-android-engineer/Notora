package com.example.notora.data.repository

import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.entity.CategoryEntity
import com.example.notora.domain.model.Category
import com.example.notora.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map {
                Category(
                    id = it.id,
                    name = it.name,
                    colorHex = it.colorHex,
                    iconName = it.iconName,
                    displayOrder = it.displayOrder,
                    isArchived = it.isArchived
                )
            }
        }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.let {
            Category(
                id = it.id,
                name = it.name,
                colorHex = it.colorHex,
                iconName = it.iconName,
                displayOrder = it.displayOrder,
                isArchived = it.isArchived
            )
        }
    }

    override suspend fun saveCategory(category: Category): Long {
        val entity = CategoryEntity(
            id = category.id,
            name = category.name,
            colorHex = category.colorHex,
            iconName = category.iconName,
            displayOrder = category.displayOrder,
            isArchived = category.isArchived
        )
        return if (category.id == 0L) {
            categoryDao.insertCategory(entity)
        } else {
            categoryDao.updateCategory(entity)
            category.id
        }
    }

    override suspend fun deleteCategory(id: Long) {
        categoryDao.deleteCategoryById(id)
    }
}
