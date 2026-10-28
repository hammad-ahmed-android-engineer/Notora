package com.example.notora.presentation.revisions

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.NoteRevision
import com.example.notora.domain.usecase.revision.GetRevisionsUseCase
import com.example.notora.domain.usecase.revision.RestoreRevisionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class NoteRevisionsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val revisions: List<NoteRevision> = emptyList()
)

@HiltViewModel
class NoteRevisionsViewModel @Inject constructor(
    private val getRevisionsUseCase: GetRevisionsUseCase,
    private val restoreRevisionUseCase: RestoreRevisionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navNoteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L

    val uiState: StateFlow<NoteRevisionsUiState> = getRevisionsUseCase(navNoteId)
        .map { list -> NoteRevisionsUiState(isLoading = false, errorMessage = null, revisions = list) }
        .catch { e -> emit(NoteRevisionsUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load revisions.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoteRevisionsUiState(isLoading = true)
        )

    fun getRevisions(noteId: Long): StateFlow<NoteRevisionsUiState> {
        return if (noteId == navNoteId) {
            uiState
        } else {
            getRevisionsUseCase(noteId)
                .map { list -> NoteRevisionsUiState(isLoading = false, errorMessage = null, revisions = list) }
                .catch { e -> emit(NoteRevisionsUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load revisions.")) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = NoteRevisionsUiState(isLoading = true)
                )
        }
    }

    fun restoreRevision(noteId: Long, revision: NoteRevision, onRestored: () -> Unit) {
        viewModelScope.launch {
            val success = restoreRevisionUseCase(noteId, revision)
            if (success) {
                onRestored()
            }
        }
    }
}
