package com.example.notora.ui.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.NoteTemplate
import com.example.notora.domain.usecase.template.CreateNoteFromTemplateUseCase
import com.example.notora.domain.usecase.template.DeleteTemplateUseCase
import com.example.notora.domain.usecase.template.GetTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for viewing, creating notes from, and managing templates.
 */
@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val createNoteFromTemplateUseCase: CreateNoteFromTemplateUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            getTemplatesUseCase.seedDefaultsIfEmpty()
        }
    }

    val templates: StateFlow<List<NoteTemplate>> = getTemplatesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Instantiates a new note based on the provided template and executes callback on completion.
     */
    fun createNoteFromTemplate(template: NoteTemplate, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = createNoteFromTemplateUseCase(template)
            onCreated(newId)
        }
    }

    /**
     * Deletes a template by its ID.
     */
    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            deleteTemplateUseCase(id)
        }
    }
}
