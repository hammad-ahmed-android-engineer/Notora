package com.example.notora.presentation.tags

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Tag
import com.example.notora.domain.usecase.tag.DeleteTagUseCase
import com.example.notora.domain.usecase.tag.GetTagsUseCase
import com.example.notora.domain.usecase.tag.SaveTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TagsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tags: List<Tag> = emptyList()
)

@HiltViewModel
class TagViewModel @Inject constructor(
    getTagsUseCase: GetTagsUseCase,
    private val saveTagUseCase: SaveTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : ViewModel() {

    val uiState: StateFlow<TagsUiState> = getTagsUseCase()
        .map { list -> TagsUiState(isLoading = false, errorMessage = null, tags = list) }
        .catch { e -> emit(TagsUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load tags.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TagsUiState(isLoading = true)
        )

    fun createTag(name: String) {
        viewModelScope.launch {
            saveTagUseCase(name)
        }
    }

    fun deleteTag(id: Long) {
        viewModelScope.launch {
            deleteTagUseCase(id)
        }
    }
}
