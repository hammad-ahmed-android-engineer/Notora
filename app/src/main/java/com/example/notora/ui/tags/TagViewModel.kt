package com.example.notora.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Tag
import com.example.notora.domain.usecase.tag.DeleteTagUseCase
import com.example.notora.domain.usecase.tag.GetTagsUseCase
import com.example.notora.domain.usecase.tag.SaveTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for listing, creating, and deleting note tags.
 */
@HiltViewModel
class TagViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
    private val saveTagUseCase: SaveTagUseCase,
    private val deleteTagUseCase: DeleteTagUseCase
) : ViewModel() {

    val tags: StateFlow<List<Tag>> = getTagsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Cleans, validates, and persists a new tag.
     */
    fun createTag(name: String) {
        viewModelScope.launch {
            saveTagUseCase(name)
        }
    }

    /**
     * Deletes a tag by its ID.
     */
    fun deleteTag(id: Long) {
        viewModelScope.launch {
            deleteTagUseCase(id)
        }
    }
}
