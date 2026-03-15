package com.docvault.data.repository

import com.docvault.core.common.result.DocVaultResult
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.database.entity.DocumentEntity
import com.docvault.data.filesystem.SecureFileManager
import com.docvault.data.mapper.toDomain
import com.docvault.domain.model.DocumentType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DocumentRepositoryImplTest {

    private val documentDao: DocumentDao = mockk()
    private val secureFileManager: SecureFileManager = mockk()

    private lateinit var repository: DocumentRepositoryImpl

    private val fakeEntity = DocumentEntity(
        id = "1",
        name = "test.jpg",
        type = "IMAGE",
        encryptedFilePath = "/path/test.jpg",
        createdAt = 1000L,
        fileSize = 1024L,
        latitude = null,
        longitude = null,
        address = null
    )

    private val fakeDocument = fakeEntity.toDomain()

    @Before
    fun setup() {
        repository = DocumentRepositoryImpl(documentDao, secureFileManager)
    }

    @Test
    fun `getAllDocuments returns mapped documents from DAO`() = runTest {
        every { documentDao.getAllDocuments() } returns flowOf(listOf(fakeEntity))

        repository.getAllDocuments()
            .filter { it is DocVaultResult.Success }
            .collect { result ->
                assertEquals(1, (result as DocVaultResult.Success).data.size)
                assertEquals(fakeDocument, result.data.first())
            }
    }

    @Test
    fun `getDocumentsByType returns filtered documents from DAO`() = runTest {
        every { documentDao.getDocumentsByType("IMAGE") } returns flowOf(listOf(fakeEntity))

        repository.getDocumentsByType(DocumentType.IMAGE)
            .filter { it is DocVaultResult.Success }
            .collect { result ->
                assertEquals(1, (result as DocVaultResult.Success).data.size)
            }
    }

    @Test
    fun `getDocumentById returns mapped document`() = runTest {
        coEvery { documentDao.getDocumentById("1") } returns fakeEntity

        val result = repository.getDocumentById("1")

        assertTrue(result is DocVaultResult.Success)
        assertEquals(fakeDocument, (result as DocVaultResult.Success).data)
    }

    @Test
    fun `getDocumentById returns Error when document not found`() = runTest {
        coEvery { documentDao.getDocumentById("99") } returns null

        val result = repository.getDocumentById("99")

        assertTrue(result is DocVaultResult.Error)
    }

    @Test
    fun `addDocument calls secureFileManager and dao`() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        coEvery { secureFileManager.saveFile(any(), bytes) } returns "/encrypted/path"
        coEvery { documentDao.insertDocument(any()) } returns Unit

        val result = repository.addDocument(fakeDocument, bytes)

        assertTrue(result is DocVaultResult.Success)
        coVerify { secureFileManager.saveFile(any(), bytes) }
        coVerify { documentDao.insertDocument(any()) }
    }

    @Test
    fun `deleteDocument calls secureFileManager and dao`() = runTest {
        coEvery { secureFileManager.deleteFile(fakeDocument.filePath) } returns true
        coEvery { documentDao.deleteDocument(fakeEntity) } returns Unit

        val result = repository.deleteDocument(fakeDocument)

        assertTrue(result is DocVaultResult.Success)
        coVerify { secureFileManager.deleteFile(fakeDocument.filePath) }
        coVerify { documentDao.deleteDocument(fakeEntity) }
    }
}