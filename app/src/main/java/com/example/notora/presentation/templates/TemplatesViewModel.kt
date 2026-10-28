package com.example.notora.presentation.templates

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notora.domain.model.NoteTemplate
import com.example.notora.domain.usecase.template.CreateNoteFromTemplateUseCase
import com.example.notora.domain.usecase.template.DeleteTemplateUseCase
import com.example.notora.domain.usecase.template.GetTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TemplatesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val templates: List<NoteTemplate> = emptyList()
)

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    getTemplatesUseCase: GetTemplatesUseCase,
    private val createNoteFromTemplateUseCase: CreateNoteFromTemplateUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            getTemplatesUseCase.seedDefaultsIfEmpty()
        }
    }

    val uiState: StateFlow<TemplatesUiState> = getTemplatesUseCase()
        .map { list -> TemplatesUiState(isLoading = false, errorMessage = null, templates = list) }
        .catch { e -> emit(TemplatesUiState(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to load templates.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TemplatesUiState(isLoading = true)
        )

    fun createNoteFromTemplate(template: NoteTemplate, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = createNoteFromTemplateUseCase(template)
            onCreated(newId)
        }
    }

    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            deleteTemplateUseCase(id)
        }
    }
}
