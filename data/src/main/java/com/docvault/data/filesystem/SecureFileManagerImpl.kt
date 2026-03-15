package com.docvault.data.filesystem

import android.content.Context
import com.docvault.lib.security.crypto.CryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SecureFileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
) : SecureFileManager {
    private val storageDir: File
        get() =
            File(context.filesDir, "secure_docs").apply {
                if (!exists()) mkdirs()
            }

    override suspend fun saveFile(
        fileName: String,
        bytes: ByteArray,
    ): String =
        withContext(Dispatchers.IO) {
            val encryptedBytes = cryptoManager.encrypt(bytes)
            val file = File(storageDir, fileName)
            file.writeBytes(encryptedBytes)
            file.absolutePath
        }

    override suspend fun readFile(filePath: String): ByteArray =
        withContext(Dispatchers.IO) {
            val encryptedBytes = File(filePath).readBytes()
            cryptoManager.decrypt(encryptedBytes)
        }

    override suspend fun deleteFile(filePath: String): Boolean =
        withContext(Dispatchers.IO) {
            File(filePath).delete()
        }
}
