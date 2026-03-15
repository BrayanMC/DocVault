package com.docvault.data.mappers

import com.docvault.data.database.entity.AccessLogEntity
import com.docvault.data.mapper.toDomain
import com.docvault.data.mapper.toEntity
import com.docvault.domain.model.AccessLog
import org.junit.Assert.assertEquals
import org.junit.Test

class AccessLogMapperTest {

    private val fakeEntity = AccessLogEntity(
        id = 1,
        documentId = "doc-1",
        accessedAt = 1000L
    )

    private val fakeDomain = AccessLog(
        id = 1,
        documentId = "doc-1",
        accessedAt = 1000L
    )

    @Test
    fun `toDomain maps all fields correctly`() {
        val result = fakeEntity.toDomain()
        assertEquals(fakeEntity.id, result.id)
        assertEquals(fakeEntity.documentId, result.documentId)
        assertEquals(fakeEntity.accessedAt, result.accessedAt)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val result = fakeDomain.toEntity()
        assertEquals(fakeDomain.id, result.id)
        assertEquals(fakeDomain.documentId, result.documentId)
        assertEquals(fakeDomain.accessedAt, result.accessedAt)
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
}