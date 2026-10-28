package com.example.notora.presentation.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SearchUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val results: List<Note> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val filter: NoteFilter = NoteFilter(),
    val resultCount: Int = 0
)

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
    private val duplicateNoteUseCase: DuplicateNoteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialQuery: String = savedStateHandle.get<String>("search_query") ?: ""

    private val _filter = MutableStateFlow(NoteFilter(query = initialQuery))
    val filter: StateFlow<NoteFilter> = _filter.asStateFlow()

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
            isLoading = false,
            errorMessage = null,
            results = results,
            categories = categories,
            tags = tags,
            filter = filter,
            resultCount = results.size
        )
    }.catch { e ->
        emit(SearchUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Search operation failed."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isLoading = true)
    )

    fun updateQuery(query: String) {
        savedStateHandle["search_query"] = query
        _filter.value = _filter.value.copy(query = query)
    }

    fun updateFilter(filter: NoteFilter) {
        savedStateHandle["search_query"] = filter.query
        _filter.value = filter
    }

    fun clearQuery() {
        savedStateHandle["search_query"] = ""
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
