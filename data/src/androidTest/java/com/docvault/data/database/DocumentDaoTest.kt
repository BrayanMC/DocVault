package com.docvault.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.database.entity.DocumentEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DocumentDaoTest {

    private lateinit var database: DocVaultDatabase
    private lateinit var dao: DocumentDao

    private val fakeDocument = DocumentEntity(
        id = "1",
        name = "test.pdf",
        type = "PDF",
        encryptedFilePath = "/path/test.pdf",
        createdAt = 1000L,
        fileSize = 2048L,
        latitude = -12.0,
        longitude = -77.0,
        address = "Av. Test 123, Lima"
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DocVaultDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.documentDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllDocuments() = runTest {
        dao.insertDocument(fakeDocument)
        val documents = dao.getAllDocuments().first()
        assertEquals(1, documents.size)
        assertEquals(fakeDocument, documents.first())
    }

    @Test
    fun insertAndGetDocumentById() = runTest {
        dao.insertDocument(fakeDocument)
        val result = dao.getDocumentById("1")
        assertEquals(fakeDocument, result)
    }

    @Test
    fun getDocumentById_returnsNull_whenNotFound() = runTest {
        val result = dao.getDocumentById("nonexistent")
        assertNull(result)
    }

    @Test
    fun insertAndGetDocumentsByType() = runTest {
        val pdfDoc = fakeDocument.copy(id = "1", type = "PDF")
        val imageDoc = fakeDocument.copy(id = "2", type = "IMAGE")
        dao.insertDocument(pdfDoc)
        dao.insertDocument(imageDoc)

        val pdfs = dao.getDocumentsByType("PDF").first()
        assertEquals(1, pdfs.size)
        assertEquals("PDF", pdfs.first().type)
    }

    @Test
    fun deleteDocument() = runTest {
        dao.insertDocument(fakeDocument)
        dao.deleteDocument(fakeDocument)
        val result = dao.getDocumentById("1")
        assertNull(result)
    }

    @Test
    fun insertMultipleDocuments_getAllReturnsAll() = runTest {
        val doc1 = fakeDocument.copy(id = "1")
        val doc2 = fakeDocument.copy(id = "2")
        val doc3 = fakeDocument.copy(id = "3")
        dao.insertDocument(doc1)
        dao.insertDocument(doc2)
        dao.insertDocument(doc3)

        val documents = dao.getAllDocuments().first()
        assertEquals(3, documents.size)
    }
}