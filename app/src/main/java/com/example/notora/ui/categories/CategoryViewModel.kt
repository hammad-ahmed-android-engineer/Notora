package com.example.notora.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Category
import com.example.notora.domain.usecase.category.DeleteCategoryUseCase
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.category.SaveCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for listing, creating, updating, and deleting categories.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Validates and saves a category.
     */
    fun saveCategory(id: Long, name: String, colorHex: String) {
        viewModelScope.launch {
            saveCategoryUseCase(id, name, colorHex)
        }
    }

    /**
     * Deletes a category by its ID.
     */
    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(id)
        }
    }
}
