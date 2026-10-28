package com.example.notora.presentation.categories

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Category
import com.example.notora.domain.usecase.category.DeleteCategoryUseCase
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.category.SaveCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class CategoriesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<Category> = emptyList()
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    getCategoriesUseCase: GetCategoriesUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    val uiState: StateFlow<CategoriesUiState> = getCategoriesUseCase()
        .map { list -> CategoriesUiState(isLoading = false, errorMessage = null, categories = list) }
        .catch { e -> emit(CategoriesUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load categories.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoriesUiState(isLoading = true)
        )

    fun saveCategory(id: Long, name: String, colorHex: String) {
        viewModelScope.launch {
            saveCategoryUseCase(id, name, colorHex)
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(id)
        }
    }
}
