package com.docvault.lib.camera.model

data class CameraResult(
    val filePath: String,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CameraResult) return false
        return filePath == other.filePath && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = filePath.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
