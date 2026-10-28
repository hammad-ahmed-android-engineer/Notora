package com.example.notora.presentation.archive

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Note
import com.example.notora.domain.usecase.note.ArchiveNoteUseCase
import com.example.notora.domain.usecase.note.GetArchivedNotesUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class ArchiveUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val archivedNotes: List<Note> = emptyList()
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    getArchivedNotesUseCase: GetArchivedNotesUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val trashNoteUseCase: TrashNoteUseCase
) : ViewModel() {

    val uiState: StateFlow<ArchiveUiState> = getArchivedNotesUseCase()
        .map { notes ->
            ArchiveUiState(isLoading = false, errorMessage = null, archivedNotes = notes)
        }
        .catch { e ->
            emit(ArchiveUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load archived notes."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ArchiveUiState(isLoading = true)
        )

    fun unarchiveNote(noteId: Long) {
        viewModelScope.launch {
            archiveNoteUseCase.unarchive(noteId)
        }
    }

    fun trashNote(noteId: Long) {
        viewModelScope.launch {
            trashNoteUseCase(noteId)
        }
    }
}
