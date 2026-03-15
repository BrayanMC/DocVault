package com.docvault.data.filesystem

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DocumentFileManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getFileName(uri: Uri): String {
        var name = "documento_${System.currentTimeMillis()}"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                name = cursor.getString(index)
            }
        }
        return name
    }

    fun getMimeType(uri: Uri): String? = context.contentResolver.getType(uri)

    fun readBytes(uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
}
