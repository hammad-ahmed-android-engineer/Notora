package com.example.notora.domain.usecase.note

import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.model.Note
import com.example.notora.domain.model.NoteFilter
import com.example.notora.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to search and filter notes with sanitized search queries.
 */
class SearchNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(filter: NoteFilter): Flow<List<Note>> {
        val sanitizedQuery = AppUtils.cleanSearchQuery(filter.query)
        val sanitizedFilter = filter.copy(query = sanitizedQuery)
        return noteRepository.searchNotes(sanitizedFilter)
    }
}
