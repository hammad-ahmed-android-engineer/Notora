package com.example.notora.ui.revisions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.NoteRevision
import com.example.notora.domain.usecase.revision.GetRevisionsUseCase
import com.example.notora.domain.usecase.revision.RestoreRevisionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for inspecting and restoring note revision history.
 */
@HiltViewModel
class NoteRevisionsViewModel @Inject constructor(
    private val getRevisionsUseCase: GetRevisionsUseCase,
    private val restoreRevisionUseCase: RestoreRevisionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navNoteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L

    val revisions: StateFlow<List<NoteRevision>> = getRevisionsUseCase(navNoteId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getRevisions(noteId: Long): StateFlow<List<NoteRevision>> {
        return if (noteId == navNoteId) {
            revisions
        } else {
            getRevisionsUseCase(noteId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        }
    }

    /**
     * Restores the specified revision to the note and invokes completion callback.
     */
    fun restoreRevision(noteId: Long, revision: NoteRevision, onRestored: () -> Unit) {
        viewModelScope.launch {
            val success = restoreRevisionUseCase(noteId, revision)
            if (success) {
                onRestored()
            }
        }
    }
}
