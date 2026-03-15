package com.docvault.data.mappers

import com.docvault.data.database.entity.DocumentEntity
import com.docvault.data.mapper.toDomain
import com.docvault.data.mapper.toEntity
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentMapperTest {

    private val fakeEntity = DocumentEntity(
        id = "1",
        name = "test.pdf",
        type = "PDF",
        encryptedFilePath = "/encrypted/test.pdf",
        createdAt = 1000L,
        fileSize = 2048L,
        latitude = -12.0,
        longitude = -77.0,
        address = "Av. Test 123, Lima"
    )

    private val fakeDomain = Document(
        id = "1",
        name = "test.pdf",
        type = DocumentType.PDF,
        filePath = "/encrypted/test.pdf",
        createdAt = 1000L,
        fileSize = 2048L,
        latitude = -12.0,
        longitude = -77.0,
        address = "Av. Test 123, Lima"
    )

    @Test
    fun `toDomain maps all fields correctly`() {
        val result = fakeEntity.toDomain()
        assertEquals(fakeEntity.id, result.id)
        assertEquals(fakeEntity.name, result.name)
        assertEquals(DocumentType.PDF, result.type)
        assertEquals(fakeEntity.encryptedFilePath, result.filePath)
        assertEquals(fakeEntity.createdAt, result.createdAt)
        assertEquals(fakeEntity.fileSize, result.fileSize)
        assertEquals(fakeEntity.latitude, result.latitude)
        assertEquals(fakeEntity.longitude, result.longitude)
        assertEquals(fakeEntity.address, result.address)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val result = fakeDomain.toEntity()
        assertEquals(fakeDomain.id, result.id)
        assertEquals(fakeDomain.name, result.name)
        assertEquals("PDF", result.type)
        assertEquals(fakeDomain.filePath, result.encryptedFilePath)
        assertEquals(fakeDomain.createdAt, result.createdAt)
        assertEquals(fakeDomain.fileSize, result.fileSize)
        assertEquals(fakeDomain.latitude, result.latitude)
        assertEquals(fakeDomain.longitude, result.longitude)
        assertEquals(fakeDomain.address, result.address)
    }

    @Test
    fun `toDomain then toEntity is identity`() {
        val result = fakeEntity.toDomain().toEntity()
        assertEquals(fakeEntity, result)
    }

    @Test
    fun `toEntity then toDomain is identity`() {
        val result = fakeDomain.toEntity().toDomain()
        assertEquals(fakeDomain, result)
    }

    @Test
    fun `toDomain maps IMAGE type correctly`() {
        val entity = fakeEntity.copy(type = "IMAGE")
        assertEquals(DocumentType.IMAGE, entity.toDomain().type)
    }

    @Test
    fun `toDomain maps null location fields correctly`() {
        val entity = fakeEntity.copy(latitude = null, longitude = null, address = null)
        val result = entity.toDomain()
        assertEquals(null, result.latitude)
        assertEquals(null, result.longitude)
        assertEquals(null, result.address)
    }
}