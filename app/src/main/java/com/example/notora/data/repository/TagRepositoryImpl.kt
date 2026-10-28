package com.example.notora.data.repository

import com.example.notora.data.local.dao.TagDao
import com.example.notora.data.local.entity.TagEntity
import com.example.notora.data.local.entity.toDomainModel
import com.example.notora.domain.model.Tag
import com.example.notora.domain.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags()
            .map { list -> list.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun saveTag(name: String): Long {
        val existing = tagDao.getTagByName(name.trim())
        if (existing != null) return existing.id
        return tagDao.insertTag(TagEntity(name = name.trim()))
    }

    override suspend fun deleteTag(id: Long) {
        tagDao.deleteTagById(id)
    }
}
