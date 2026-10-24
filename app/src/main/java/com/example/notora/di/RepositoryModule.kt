package com.example.notora.di

import com.example.notora.data.repository.CategoryRepositoryImpl
import com.example.notora.data.repository.NoteRepositoryImpl
import com.example.notora.data.repository.SettingsRepositoryImpl
import com.example.notora.data.repository.TagRepositoryImpl
import com.example.notora.data.repository.TemplateRepositoryImpl
import com.example.notora.domain.repository.CategoryRepository
import com.example.notora.domain.repository.NoteRepository
import com.example.notora.domain.repository.SettingsRepository
import com.example.notora.domain.repository.TagRepository
import com.example.notora.domain.repository.TemplateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTemplateRepository(impl: TemplateRepositoryImpl): TemplateRepository
}
