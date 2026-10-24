package com.example.notora.ui.revisions

import androidx.lifecycle.SavedStateHandle
import com.example.notora.domain.model.NoteRevision
import com.example.notora.domain.repository.NoteRepository
import com.example.notora.domain.usecase.revision.GetRevisionsUseCase
import com.example.notora.domain.usecase.revision.RestoreRevisionUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRevisionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val noteRepository: NoteRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRevisionsFlowCreatedOnceAndShared() = runTest {
        val revs = listOf(
            NoteRevision(id = 1L, noteId = 99L, title = "Rev 1", contentJson = "{}", createdAt = 1000L)
        )
        every { noteRepository.getRevisions(99L) } returns flowOf(revs)

        val savedStateHandle = SavedStateHandle(mapOf("noteId" to 99L))
        val getRevisionsUseCase = GetRevisionsUseCase(noteRepository)
        val restoreRevisionUseCase = RestoreRevisionUseCase(noteRepository)
        val viewModel = NoteRevisionsViewModel(getRevisionsUseCase, restoreRevisionUseCase, savedStateHandle)

        // Calling getRevisions with the same noteId returns the cached StateFlow without re-querying
        val flow1 = viewModel.getRevisions(99L)
        val flow2 = viewModel.getRevisions(99L)

        assertEquals(flow1, flow2)

        // Subscribing to StateFlow triggers upstream emission
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            flow1.collect {}
        }
        testScheduler.advanceUntilIdle()

        assertEquals(revs, flow1.value)
    }
}
