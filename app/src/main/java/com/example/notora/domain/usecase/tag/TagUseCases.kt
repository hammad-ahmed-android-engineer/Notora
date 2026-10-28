package com.example.notora.domain.usecase.tag

import com.example.notora.core.utils.AppUtils
import com.example.notora.domain.model.Tag
import com.example.notora.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all tags.
 */
class GetTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(): Flow<List<Tag>> {
        return tagRepository.getAllTags()
    }
}

/**
 * Use case to clean, validate and save a tag.
 */
class SaveTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(name: String): Boolean {
        val cleanName = AppUtils.cleanTagName(name)
        if (!AppUtils.isValidTagName(cleanName)) return false
        tagRepository.saveTag(cleanName)
        return true
    }
}

/**
 * Use case to delete a tag by its ID.
 */
class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(id: Long) {
        tagRepository.deleteTag(id)
    }
}
