package com.example.notora.presentation.calendar

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType
import com.example.notora.domain.usecase.note.GetActiveNotesUseCase
import com.example.notora.domain.usecase.note.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class CalendarUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val notesForDate: List<Note> = emptyList(),
    val formattedDate: String = ""
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    getActiveNotesUseCase: GetActiveNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val uiState: StateFlow<CalendarUiState> = combine(
        getActiveNotesUseCase(),
        _selectedDate
    ) { notes, date ->
        val selectedDayKey = AppUtils.formatDayKey(date)

        val matching = notes.filter { note ->
            AppUtils.formatDayKey(note.createdAt) == selectedDayKey ||
                    AppUtils.formatDayKey(note.updatedAt) == selectedDayKey
        }

        CalendarUiState(
            isLoading = false,
            errorMessage = null,
            selectedDate = date,
            notesForDate = matching,
            formattedDate = AppUtils.formatFullDate(date)
        )
    }.catch { e ->
        emit(CalendarUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load calendar notes."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(isLoading = true)
    )

    fun selectDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    fun createDailyNote(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val dateStr = AppUtils.formatDate(_selectedDate.value)
            val title = "Daily Note - $dateStr"

            val templateDoc = RichTextDocument.fromPlainText("Today's Reflection:\n\nKey Tasks:\n- [ ] ")
            val newNote = Note(
                id = 0,
                title = title,
                contentJson = RichTextSerializer.toJson(templateDoc),
                plainTextContent = templateDoc.text,
                noteType = NoteType.JOURNAL,
                createdAt = _selectedDate.value,
                updatedAt = System.currentTimeMillis()
            )
            val newId = saveNoteUseCase(newNote)
            onCreated(newId)
        }
    }
}
