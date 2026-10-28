package com.example.notora.presentation.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteDateGroup
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.model.Tag
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.note.ArchiveNoteUseCase
import com.example.notora.domain.usecase.note.DuplicateNoteUseCase
import com.example.notora.domain.usecase.note.SearchNotesUseCase
import com.example.notora.domain.usecase.note.ToggleFavoriteUseCase
import com.example.notora.domain.usecase.note.TogglePinUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import com.example.notora.domain.usecase.settings.GetLayoutModeUseCase
import com.example.notora.domain.usecase.settings.SetLayoutModeUseCase
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
data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val notes: List<Note> = emptyList(),
    val pinnedNotes: List<Note> = emptyList(),
    val dateGroups: List<NoteDateGroup> = emptyList(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val filter: NoteFilter = NoteFilter(),
    val layoutMode: String = "STAGGERED_GRID"
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchNotesUseCase: SearchNotesUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    getTagsUseCase: GetTagsUseCase,
    getLayoutModeUseCase: GetLayoutModeUseCase,
    private val setLayoutModeUseCase: SetLayoutModeUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val trashNoteUseCase: TrashNoteUseCase,
    private val duplicateNoteUseCase: DuplicateNoteUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(NoteFilter())
    val filter = _filter.asStateFlow()

    private val notesFlow = _filter.flatMapLatest { currentFilter ->
        searchNotesUseCase(currentFilter)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        notesFlow,
        getCategoriesUseCase(),
        getTagsUseCase(),
        getLayoutModeUseCase(),
        _filter
    ) { notes, categories, tags, layoutMode, currentFilter ->
        val pinned = notes.filter { it.isPinned }
        val remaining = notes.filter { !it.isPinned }
        val dateGroups = AppUtils.groupNotesByDate(
            notes = remaining,
            sortOption = currentFilter.sortOption
        )
        HomeUiState(
            isLoading = false,
            errorMessage = null,
            notes = remaining,
            pinnedNotes = pinned,
            dateGroups = dateGroups,
            categories = categories,
            tags = tags,
            filter = currentFilter,
            layoutMode = layoutMode
        )
    }.catch { e ->
        emit(HomeUiState(isLoading = false, errorMessage = e.localizedMessage ?: "An error occurred while loading notes"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun updateFilter(newFilter: NoteFilter) {
        _filter.value = newFilter
    }

    fun updateSearchQuery(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    fun toggleLayoutMode() {
        viewModelScope.launch {
            val current = uiState.value.layoutMode
            val next = if (current == "STAGGERED_GRID") "SINGLE_COLUMN_LIST" else "STAGGERED_GRID"
            setLayoutModeUseCase(next)
        }
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
