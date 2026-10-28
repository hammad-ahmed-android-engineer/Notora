package com.example.notora.data.repository

import com.example.notora.data.local.dao.TemplateDao
import com.example.notora.data.local.entity.NoteTemplateEntity
import com.example.notora.data.local.entity.toDomainModel
import com.example.notora.data.local.entity.toEntity
import com.example.notora.domain.editor.RichTextDocument
import com.example.notora.domain.editor.RichTextSerializer
import com.example.notora.domain.model.NoteTemplate
import com.example.notora.domain.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: TemplateDao
) : TemplateRepository {

    override fun getAllTemplates(): Flow<List<NoteTemplate>> {
        return templateDao.getAllTemplates()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun seedDefaultTemplatesIfEmpty() {
        val existing = templateDao.getAllTemplates().first()
        if (existing.isEmpty()) {
            val defaultList = listOf(
                NoteTemplateEntity(
                    name = "Meeting Notes",
                    description = "Capture meeting agenda, attendees, notes and action items",
                    noteType = "MEETING",
                    titleTemplate = "Meeting: ",
                    contentJsonTemplate = RichTextSerializer.toJson(
                        RichTextDocument.fromPlainText("Date & Time:\nAttendees:\n\nAgenda:\n1. \n2. \n\nDiscussion Notes:\n\nAction Items:\n- [ ] ")
                    ),
                    isDefault = true
                ),
                NoteTemplateEntity(
                    name = "Daily Journal",
                    description = "Daily reflection, key wins, and mood tracking",
                    noteType = "JOURNAL",
                    titleTemplate = "Daily Entry: ",
                    contentJsonTemplate = RichTextSerializer.toJson(
                        RichTextDocument.fromPlainText("Today's Reflection:\n\nKey Priorities:\n1. \n2. \n\nWhat I'm grateful for:\n- ")
                    ),
                    isDefault = true
                ),
                NoteTemplateEntity(
                    name = "Study Notes",
                    description = "Subject summary, core concepts, and review points",
                    noteType = "STUDY",
                    titleTemplate = "Study: ",
                    contentJsonTemplate = RichTextSerializer.toJson(
                        RichTextDocument.fromPlainText("Subject:\nChapter/Topic:\n\nCore Concepts:\n- \n\nDetailed Explanations:\n\nSummary & Key Takeaways:")
                    ),
                    isDefault = true
                ),
                NoteTemplateEntity(
                    name = "Project Plan",
                    description = "Project goals, milestones, and deliverables",
                    noteType = "DOCUMENT",
                    titleTemplate = "Project: ",
                    contentJsonTemplate = RichTextSerializer.toJson(
                        RichTextDocument.fromPlainText("Project Objective:\n\nKey Milestones:\n1. \n2. \n\nDeliverables:\n- [ ] ")
                    ),
                    isDefault = true
                )
            )
            defaultList.forEach { templateDao.insertTemplate(it) }
        }
    }

    override suspend fun saveTemplate(template: NoteTemplate): Long {
        val entity = template.toEntity()
        return templateDao.insertTemplate(entity)
    }

    override suspend fun deleteTemplate(id: Long) {
        val entity = templateDao.getTemplateById(id)
        if (entity != null) {
            templateDao.deleteTemplate(entity)
        }
    }
}
