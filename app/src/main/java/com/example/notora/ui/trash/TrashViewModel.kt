package com.example.notora.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Note
import com.example.notora.domain.usecase.note.DeleteNotePermanentlyUseCase
import com.example.notora.domain.usecase.note.EmptyTrashUseCase
import com.example.notora.domain.usecase.note.GetTrashedNotesUseCase
import com.example.notora.domain.usecase.note.RestoreNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for listing, restoring, and permanently deleting trashed notes.
 */
@HiltViewModel
class TrashViewModel @Inject constructor(
    private val getTrashedNotesUseCase: GetTrashedNotesUseCase,
    private val restoreNoteUseCase: RestoreNoteUseCase,
    private val deleteNotePermanentlyUseCase: DeleteNotePermanentlyUseCase,
    private val emptyTrashUseCase: EmptyTrashUseCase
) : ViewModel() {

    val trashedNotes: StateFlow<List<Note>> = getTrashedNotesUseCase()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Restores a trashed note back to active status.
     */
    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            restoreNoteUseCase(noteId)
        }
    }

    /**
     * Permanently deletes a single note.
     */
    fun permanentlyDelete(noteId: Long) {
        viewModelScope.launch {
            deleteNotePermanentlyUseCase(noteId)
        }
    }

    /**
     * Empties all trashed notes permanently.
     */
    fun emptyTrash() {
        viewModelScope.launch {
            emptyTrashUseCase()
        }
    }
}
