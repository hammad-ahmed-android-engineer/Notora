package com.example.notora.ui.editor

import androidx.lifecycle.SavedStateHandle
import com.example.notora.data.file.AttachmentStorageManager
import com.example.notora.data.reminder.ReminderScheduler
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.Category
import com.example.notora.domain.model.ChecklistItem
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.Tag
import com.example.notora.domain.repository.CategoryRepository
import com.example.notora.domain.repository.NoteRepository
import com.example.notora.domain.repository.TagRepository
import com.example.notora.domain.usecase.category.GetCategoriesUseCase
import com.example.notora.domain.usecase.note.DeleteNotePermanentlyUseCase
import com.example.notora.domain.usecase.note.GetNoteByIdUseCase
import com.example.notora.domain.usecase.note.SaveNoteUseCase
import com.example.notora.domain.usecase.note.TrashNoteUseCase
import com.example.notora.domain.usecase.tag.GetTagsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val noteRepository: NoteRepository = mockk(relaxed = true)
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val tagRepository: TagRepository = mockk(relaxed = true)
    private val attachmentStorageManager: AttachmentStorageManager = mockk(relaxed = true)
    private val reminderScheduler: ReminderScheduler = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { categoryRepository.getAllCategories() } returns flowOf(listOf(Category(1L, "Work", "#6366F1")))
        coEvery { tagRepository.getAllTags() } returns flowOf(listOf(Tag(1L, "urgent")))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle): NoteEditorViewModel {
        return NoteEditorViewModel(
            getNoteByIdUseCase = GetNoteByIdUseCase(noteRepository),
            saveNoteUseCase = SaveNoteUseCase(noteRepository),
            trashNoteUseCase = TrashNoteUseCase(noteRepository),
            deleteNotePermanentlyUseCase = DeleteNotePermanentlyUseCase(noteRepository),
            getCategoriesUseCase = GetCategoriesUseCase(categoryRepository),
            getTagsUseCase = GetTagsUseCase(tagRepository),
            attachmentStorageManager = attachmentStorageManager,
            reminderScheduler = reminderScheduler,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun testRestoresDraftFromSavedStateHandleWithoutCallingRepo() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "noteId" to 123L,
                "draft_title" to "Draft Note Title",
                "draft_content_json" to RichTextSerializer.toJson(RichTextDocument.fromPlainText("Draft Content")),
                "draft_color_hex" to "#FF5733"
            )
        )

        val viewModel = createViewModel(savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Draft Note Title", viewModel.uiState.value.title)
        assertEquals("Draft Content", viewModel.editorState.getDocument().text)
        assertEquals("#FF5733", viewModel.uiState.value.colorHex)

        // NoteRepository should NOT have been called because draft was restored from SavedStateHandle
        coVerify(exactly = 0) { noteRepository.getNoteByIdSync(123L) }
    }

    @Test
    fun testEditingPersistsImmediatelyToSavedStateHandle() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("noteId" to -1L))

        val viewModel = createViewModel(savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTitleChange("New Rotating Title")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("New Rotating Title", savedStateHandle.get<String>("draft_title"))

        viewModel.setChecklistItems(listOf(ChecklistItem(id = 1L, noteId = 0L, text = "Buy Groceries", isCompleted = false)))
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(savedStateHandle.get<String>("draft_checklist_json"))

        viewModel.setColorHex("#10B981")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("#10B981", savedStateHandle.get<String>("draft_color_hex"))
    }

    @Test
    fun testFreshLoadCallsRepoOnlyOnce() = runTest {
        val note = Note(
            id = 456L,
            title = "Existing Note",
            contentJson = RichTextSerializer.toJson(RichTextDocument.fromPlainText("Persisted text"))
        )
        coEvery { noteRepository.getNoteByIdSync(456L) } returns note

        val savedStateHandle = SavedStateHandle(mapOf("noteId" to 456L))

        val viewModel = createViewModel(savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Existing Note", viewModel.uiState.value.title)
        assertEquals("Persisted text", viewModel.editorState.getDocument().text)

        // Verifies repo was called only once
        coVerify(exactly = 1) { noteRepository.getNoteByIdSync(456L) }
    }
}
