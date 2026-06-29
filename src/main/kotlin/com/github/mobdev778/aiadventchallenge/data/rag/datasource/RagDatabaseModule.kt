package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Module
class RagDatabaseModule {

    @Single
    fun provideRagAppDatabase(): RagAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("rag_documents.db")
        return RagAppDatabase.create(dbFile)
    }

    @Single
    fun provideRagDocumentDao(ragAppDatabase: RagAppDatabase): RagDocumentDao {
        return ragAppDatabase.ragDocumentDao()
    }
}
