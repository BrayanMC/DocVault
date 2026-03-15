package com.docvault.data.filesystem

/**
 * Contract for saving and retrieving encrypted files
 * in the app's private storage directory.
 */
interface SecureFileManager {
    /**
     * Encrypts [bytes] and saves them to private storage.
     * Returns the absolute file path of the saved file.
     */
    suspend fun saveFile(
        fileName: String,
        bytes: ByteArray,
    ): String

    /**
     * Reads and decrypts the file at [filePath].
     * Returns the decrypted bytes.
     */
    suspend fun readFile(filePath: String): ByteArray

    /**
     * Permanently deletes the file at [filePath] from storage.
     */
    suspend fun deleteFile(filePath: String): Boolean
}
