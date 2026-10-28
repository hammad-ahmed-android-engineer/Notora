package com.example.notora.presentation.trash

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Note
import com.example.notora.domain.usecase.note.DeleteNotePermanentlyUseCase
import com.example.notora.domain.usecase.note.EmptyTrashUseCase
import com.example.notora.domain.usecase.note.GetTrashedNotesUseCase
import com.example.notora.domain.usecase.note.RestoreNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TrashUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val trashedNotes: List<Note> = emptyList()
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    getTrashedNotesUseCase: GetTrashedNotesUseCase,
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val deleteNotePermanentlyUseCase: DeleteNotePermanentlyUseCase,
    private val emptyTrashUseCase: EmptyTrashUseCase
) : ViewModel() {

    val uiState: StateFlow<TrashUiState> = getTrashedNotesUseCase()
        .map { notes ->
            TrashUiState(isLoading = false, errorMessage = null, trashedNotes = notes)
        }
        .catch { e ->
            emit(TrashUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load trashed notes."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrashUiState(isLoading = true)
        )

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            restoreNoteUseCase(noteId)
        }
    }

    fun permanentlyDelete(noteId: Long) {
        viewModelScope.launch {
            deleteNotePermanentlyUseCase(noteId)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            emptyTrashUseCase()
        }
    }
}
