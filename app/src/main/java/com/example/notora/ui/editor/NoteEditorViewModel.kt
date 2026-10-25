package com.example.notora.ui.editor

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.common.util.AppUtils
import com.example.notora.data.file.AttachmentStorageManager
import com.example.notora.data.reminder.ReminderScheduler
import com.example.notora.domain.editor.RichTextEditorState
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.Attachment
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.ChecklistItem
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteType
import com.example.notora.domain.model.Tag
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.note.DeleteNotePermanentlyUseCase
import com.example.notora.domain.usecase.note.GetNoteByIdUseCase
import com.example.notora.domain.usecase.note.SaveNoteUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import com.example.notora.domain.usecase.tag.GetTagsUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class NoteEditorUiState(
    val noteId: Long = 0,
    val title: String = "",
    val noteType: NoteType = NoteType.STANDARD,
    val category: Category? = null,
    val tags: List<Tag> = emptyList(),
    val colorHex: String = "#FFFFFF",
    val checklistItems: List<ChecklistItem> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val reminderTime: Long? = null,
    val isReminderActive: Boolean = false,
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val readingTimeMinutes: Int = 0,
    val availableCategories: List<Category> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val isSaving: Boolean = false,
    val lastSavedMessage: String? = null
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val trashNoteUseCase: TrashNoteUseCase,
    private val deleteNotePermanentlyUseCase: DeleteNotePermanentlyUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val attachmentStorageManager: AttachmentStorageManager,
    private val reminderScheduler: ReminderScheduler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val navNoteId: Long? = savedStateHandle.get<Long>("noteId")?.takeIf { it != -1L }
    private val navNoteType: String? = savedStateHandle.get<String>("noteType")
    private val gson = Gson()

    val editorState = RichTextEditorState()

    var titleTextFieldValue by mutableStateOf(TextFieldValue(""))
        private set

    private val _uiState = MutableStateFlow(
        NoteEditorUiState(
            noteId = savedStateHandle.get<Long>("draft_note_id") ?: (navNoteId ?: 0L),
            noteType = try {
                NoteType.valueOf(navNoteType ?: "STANDARD")
            } catch (_: Exception) {
                NoteType.STANDARD
            }
        )
    )
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null
    private var isDataLoaded = false

    init {
        observeCategoriesAndTags()
        loadData()
    }

    private fun observeCategoriesAndTags() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.value = _uiState.value.copy(availableCategories = categories)
            }
        }
        viewModelScope.launch {
            getTagsUseCase().collect { tags ->
                _uiState.value = _uiState.value.copy(availableTags = tags)
            }
        }
    }

    private fun loadData() {
        if (isDataLoaded) return
        isDataLoaded = true

        viewModelScope.launch {
            // 1. Check if there is already an in-memory/persisted draft in SavedStateHandle
            val draftTitle: String? = savedStateHandle.get<String>("draft_title")
            val draftContentJson: String? = savedStateHandle.get<String>("draft_content_json")

            if (draftTitle != null || draftContentJson != null) {
                draftContentJson?.let {
                    val doc = RichTextSerializer.fromJson(it)
                    editorState.setDocument(doc)
                }
                val title = draftTitle ?: ""
                titleTextFieldValue = TextFieldValue(title, TextRange(title.length))

                val draftChecklistJson: String? = savedStateHandle.get<String>("draft_checklist_json")
                val checklistItems: List<ChecklistItem> = if (!draftChecklistJson.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<ChecklistItem>>() {}.type
                        gson.fromJson(draftChecklistJson, type) ?: emptyList()
                    } catch (_: Exception) { emptyList() }
                } else emptyList()

                val draftAttachmentsJson: String? = savedStateHandle.get<String>("draft_attachments_json")
                val attachments: List<Attachment> = if (!draftAttachmentsJson.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<Attachment>>() {}.type
                        gson.fromJson(draftAttachmentsJson, type) ?: emptyList()
                    } catch (_: Exception) { emptyList() }
                } else emptyList()

                val draftCategoryJson: String? = savedStateHandle.get<String>("draft_category_json")
                val category: Category? = if (!draftCategoryJson.isNullOrBlank()) {
                    try { gson.fromJson(draftCategoryJson, Category::class.java) } catch (_: Exception) { null }
                } else null

                val draftTagsJson: String? = savedStateHandle.get<String>("draft_tags_json")
                val tags: List<Tag> = if (!draftTagsJson.isNullOrBlank()) {
                    try {
                        val type = object : TypeToken<List<Tag>>() {}.type
                        gson.fromJson(draftTagsJson, type) ?: emptyList()
                    } catch (_: Exception) { emptyList() }
                } else emptyList()

                val colorHex = savedStateHandle.get<String>("draft_color_hex") ?: "#FFFFFF"
                val isPinned = savedStateHandle.get<Boolean>("draft_is_pinned") ?: false
                val isFavorite = savedStateHandle.get<Boolean>("draft_is_favorite") ?: false
                val reminderTime = savedStateHandle.get<Long>("draft_reminder_time")
                val isReminderActive = savedStateHandle.get<Boolean>("draft_is_reminder_active") ?: false
                val noteId = savedStateHandle.get<Long>("draft_note_id") ?: (navNoteId ?: 0L)

                _uiState.value = _uiState.value.copy(
                    noteId = noteId,
                    title = title,
                    category = category,
                    tags = tags,
                    colorHex = colorHex,
                    checklistItems = checklistItems,
                    attachments = attachments,
                    isPinned = isPinned,
                    isFavorite = isFavorite,
                    reminderTime = reminderTime,
                    isReminderActive = isReminderActive
                )
                updateStats()
                return@launch
            }

            // 2. Initial load from repository only if opening an existing note
            if (navNoteId != null && navNoteId != 0L) {
                val existingNote = getNoteByIdUseCase.getSync(navNoteId)
                if (existingNote != null) {
                    val doc = RichTextSerializer.fromJson(existingNote.contentJson)
                    editorState.setDocument(doc)
                    titleTextFieldValue = TextFieldValue(existingNote.title, TextRange(existingNote.title.length))

                    _uiState.value = _uiState.value.copy(
                        noteId = existingNote.id,
                        title = existingNote.title,
                        noteType = existingNote.noteType,
                        category = existingNote.category,
                        tags = existingNote.tags,
                        colorHex = existingNote.colorHex,
                        checklistItems = existingNote.checklistItems,
                        attachments = existingNote.attachments,
                        isPinned = existingNote.isPinned,
                        isFavorite = existingNote.isFavorite,
                        reminderTime = existingNote.reminderTime,
                        isReminderActive = existingNote.isReminderActive,
                        wordCount = existingNote.wordCount,
                        charCount = existingNote.charCount,
                        readingTimeMinutes = existingNote.readingTimeMinutes
                    )
                    persistDraftToSavedState()
                }
            }
        }
    }

    private fun persistDraftToSavedState() {
        val state = _uiState.value
        savedStateHandle["draft_note_id"] = state.noteId
        savedStateHandle["draft_title"] = state.title
        savedStateHandle["draft_content_json"] = RichTextSerializer.toJson(editorState.getDocument())
        savedStateHandle["draft_color_hex"] = state.colorHex
        savedStateHandle["draft_is_pinned"] = state.isPinned
        savedStateHandle["draft_is_favorite"] = state.isFavorite
        savedStateHandle["draft_reminder_time"] = state.reminderTime
        savedStateHandle["draft_is_reminder_active"] = state.isReminderActive

        try {
            savedStateHandle["draft_checklist_json"] = gson.toJson(state.checklistItems)
            savedStateHandle["draft_attachments_json"] = gson.toJson(state.attachments)
            savedStateHandle["draft_category_json"] = gson.toJson(state.category)
            savedStateHandle["draft_tags_json"] = gson.toJson(state.tags)
        } catch (_: Exception) {}
    }

    fun isNoteEmpty(): Boolean {
        val doc = editorState.getDocument()
        val plainText = RichTextSerializer.extractPlainText(doc).trim()
        val hasTitle = _uiState.value.title.trim().isNotEmpty()
        val hasContent = plainText.isNotEmpty()
        val hasChecklist = _uiState.value.checklistItems.any { it.text.trim().isNotEmpty() }
        val hasAttachments = _uiState.value.attachments.isNotEmpty()
        return !hasTitle && !hasContent && !hasChecklist && !hasAttachments
    }

    fun onTitleChange(newTitle: String) {
        if (newTitle != titleTextFieldValue.text) {
            titleTextFieldValue = TextFieldValue(newTitle, TextRange(newTitle.length))
        }
        _uiState.value = _uiState.value.copy(title = newTitle)
        scheduleAutoSave()
    }

    fun onTitleTextFieldValueChange(newValue: TextFieldValue) {
        titleTextFieldValue = newValue
        if (newValue.text != _uiState.value.title) {
            _uiState.value = _uiState.value.copy(title = newValue.text)
            scheduleAutoSave()
        }
    }

    fun onContentChange() {
        scheduleAutoSave()
    }

    private fun updateStats() {
        val doc = editorState.getDocument()
        val plainText = doc.text
        val words = AppUtils.calculateWordCount(plainText)
        val chars = AppUtils.calculateCharCount(plainText)
        val readTime = AppUtils.calculateReadingTimeMinutes(words)

        _uiState.value = _uiState.value.copy(
            wordCount = words,
            charCount = chars,
            readingTimeMinutes = readTime
        )
    }

    fun setCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(category = category)
        scheduleAutoSave()
    }

    fun toggleTag(tag: Tag) {
        val currentTags = _uiState.value.tags.toMutableList()
        if (currentTags.any { it.id == tag.id }) {
            currentTags.removeAll { it.id == tag.id }
        } else {
            currentTags.add(tag)
        }
        _uiState.value = _uiState.value.copy(tags = currentTags)
        scheduleAutoSave()
    }

    fun setColorHex(colorHex: String) {
        val safeColor = if (AppUtils.isValidHexColor(colorHex)) colorHex else "#FFFFFF"
        _uiState.value = _uiState.value.copy(colorHex = safeColor)
        scheduleAutoSave()
    }

    fun setNoteType(type: NoteType) {
        _uiState.value = _uiState.value.copy(noteType = type)
        scheduleAutoSave()
    }

    fun togglePin() {
        _uiState.value = _uiState.value.copy(isPinned = !_uiState.value.isPinned)
        scheduleAutoSave()
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
        scheduleAutoSave()
    }

    fun setChecklistItems(items: List<ChecklistItem>) {
        _uiState.value = _uiState.value.copy(checklistItems = items)
        scheduleAutoSave()
    }

    fun addAttachmentUri(uri: Uri) {
        viewModelScope.launch {
            val attachment = attachmentStorageManager.saveAttachmentFromUri(uri)
            if (attachment != null) {
                _uiState.value = _uiState.value.copy(
                    attachments = _uiState.value.attachments + attachment
                )
                scheduleAutoSave()
            }
        }
    }

    fun removeAttachment(attachment: Attachment) {
        viewModelScope.launch {
            attachmentStorageManager.deleteAttachmentFile(attachment.uri)
            val remaining = _uiState.value.attachments.filter { it.uri != attachment.uri }
            _uiState.value = _uiState.value.copy(attachments = remaining)

            if (isNoteEmpty()) {
                autoSaveJob?.cancel()
                val currentId = _uiState.value.noteId
                if (currentId != 0L) {
                    deleteNotePermanentlyUseCase(currentId)
                    _uiState.value = _uiState.value.copy(noteId = 0L)
                    savedStateHandle["draft_note_id"] = 0L
                }
            } else {
                scheduleAutoSave()
            }
        }
    }

    fun setReminder(time: Long) {
        _uiState.value = _uiState.value.copy(
            reminderTime = time,
            isReminderActive = true
        )
        scheduleAutoSave()
    }

    fun clearReminder() {
        if (_uiState.value.noteId != 0L) {
            reminderScheduler.cancelReminder(_uiState.value.noteId)
        }
        _uiState.value = _uiState.value.copy(
            reminderTime = null,
            isReminderActive = false
        )
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(500.milliseconds)
            updateStats()
            persistDraftToSavedState()

            if (isNoteEmpty()) {
                val currentId = _uiState.value.noteId
                if (currentId != 0L) {
                    deleteNotePermanentlyUseCase(currentId)
                    _uiState.value = _uiState.value.copy(noteId = 0L)
                    savedStateHandle["draft_note_id"] = 0L
                }
            } else {
                saveNote()
            }
        }
    }

    suspend fun saveNote(): Long {
        // Prevent creating or keeping blank empty notes in the database
        if (isNoteEmpty()) {
            val currentId = _uiState.value.noteId
            if (currentId != 0L) {
                deleteNotePermanentlyUseCase(currentId)
                _uiState.value = _uiState.value.copy(noteId = 0L)
                savedStateHandle["draft_note_id"] = 0L
            }
            return 0L
        }

        _uiState.value = _uiState.value.copy(isSaving = true)
        val doc = editorState.getDocument()
        val contentJson = RichTextSerializer.toJson(doc)
        val plainText = RichTextSerializer.extractPlainText(doc)

        val note = Note(
            id = _uiState.value.noteId,
            title = _uiState.value.title,
            contentJson = contentJson,
            plainTextContent = plainText,
            noteType = _uiState.value.noteType,
            category = _uiState.value.category,
            tags = _uiState.value.tags,
            checklistItems = _uiState.value.checklistItems,
            attachments = _uiState.value.attachments,
            colorHex = _uiState.value.colorHex,
            isPinned = _uiState.value.isPinned,
            isFavorite = _uiState.value.isFavorite,
            reminderTime = _uiState.value.reminderTime,
            isReminderActive = _uiState.value.isReminderActive,
            wordCount = _uiState.value.wordCount,
            charCount = _uiState.value.charCount,
            readingTimeMinutes = _uiState.value.readingTimeMinutes
        )

        val savedId = saveNoteUseCase(note, createRevisionSnapshot = true)
        _uiState.value = _uiState.value.copy(
            noteId = savedId,
            isSaving = false,
            lastSavedMessage = "Saved"
        )
        savedStateHandle["noteId"] = savedId
        savedStateHandle["draft_note_id"] = savedId

        if (note.isReminderActive && note.reminderTime != null) {
            reminderScheduler.scheduleReminder(note.copy(id = savedId))
        }

        return savedId
    }

    fun deleteNote() {
        if (_uiState.value.noteId != 0L) {
            viewModelScope.launch {
                trashNoteUseCase(_uiState.value.noteId)
            }
        }
    }

    suspend fun handleBackPress() {
        autoSaveJob?.cancel()
        if (isNoteEmpty()) {
            val currentId = _uiState.value.noteId
            if (currentId != 0L) {
                deleteNotePermanentlyUseCase(currentId)
                _uiState.value = _uiState.value.copy(noteId = 0L)
                savedStateHandle["draft_note_id"] = 0L
            }
        } else {
            saveNote()
        }
    }
}
