package com.example.notora.ui.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.Note
import com.example.notora.domain.usecase.note.ArchiveNoteUseCase
import com.example.notora.domain.usecase.note.GetArchivedNotesUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for listing, unarchiving, and trashing archived notes.
 */
@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val getArchivedNotesUseCase: GetArchivedNotesUseCase,
    private val archiveNoteUseCase: ArchiveNoteUseCase,
    private val trashNoteUseCase: TrashNoteUseCase
) : ViewModel() {

    val archivedNotes: StateFlow<List<Note>> = getArchivedNotesUseCase()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Unarchives a note back to active notes.
     */
    fun unarchiveNote(noteId: Long) {
        viewModelScope.launch {
            archiveNoteUseCase.unarchive(noteId)
        }
    }

    /**
     * Moves an archived note to trash.
     */
    fun trashNote(noteId: Long) {
        viewModelScope.launch {
            trashNoteUseCase(noteId)
        }
    }
}
