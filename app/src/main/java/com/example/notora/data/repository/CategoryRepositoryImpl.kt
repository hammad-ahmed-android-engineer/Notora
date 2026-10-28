package com.example.notora.data.repository

import com.example.notora.data.local.dao.CategoryDao
import com.example.notora.data.local.entity.toDomainModel
import com.example.notora.data.local.entity.toEntity
import com.example.notora.domain.model.Category
import com.example.notora.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomainModel()
    }

    override suspend fun saveCategory(category: Category): Long {
        val entity = category.toEntity()
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
