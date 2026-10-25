package com.example.notora.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.Tag
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.note.ArchiveNoteUseCase
import com.example.notora.domain.usecase.note.DuplicateNoteUseCase
import com.example.notora.domain.usecase.note.SearchNotesUseCase
import com.example.notora.domain.usecase.note.ToggleFavoriteUseCase
import com.example.notora.domain.usecase.note.TogglePinUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import com.example.notora.domain.usecase.tag.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val results: List<Note> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val filter: NoteFilter = NoteFilter(),
    val resultCount: Int = 0
)

/**
 * ViewModel for querying, filtering, and performing actions on notes during search.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNotesUseCase: SearchNotesUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    getTagsUseCase: GetTagsUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val trashNoteUseCase: TrashNoteUseCase,
    private val duplicateNoteUseCase: DuplicateNoteUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(NoteFilter())
    val filter = _filter.asStateFlow()

    private val searchFlow = _filter.flatMapLatest { filter ->
        searchNotesUseCase(filter)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        searchFlow,
        getCategoriesUseCase(),
        getTagsUseCase(),
        _filter
    ) { results, categories, tags, filter ->
        SearchUiState(
            results = results,
            categories = categories,
            tags = tags,
            filter = filter,
            resultCount = results.size
        )
    }
    
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun updateQuery(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    fun updateFilter(filter: NoteFilter) {
        _filter.value = filter
    }

    fun clearQuery() {
        _filter.value = _filter.value.copy(query = "")
    }

    fun togglePin(noteId: Long) {
        viewModelScope.launch { togglePinUseCase(noteId) }
    }

    fun toggleFavorite(noteId: Long) {
        viewModelScope.launch { toggleFavoriteUseCase(noteId) }
    }

    fun archiveNote(noteId: Long) {
        viewModelScope.launch { archiveNoteUseCase(noteId) }
    }

    fun trashNote(noteId: Long) {
        viewModelScope.launch { trashNoteUseCase(noteId) }
    }

    fun duplicateNote(note: Note) {
        viewModelScope.launch { duplicateNoteUseCase(note) }
    }
}
