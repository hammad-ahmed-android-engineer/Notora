package com.example.notora.domain.repository

import com.example.notora.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun saveCategory(category: Category): Long
    suspend fun deleteCategory(id: Long)
}
