package com.docvault.lib.security.crypto

/**
 * Contract for AES-256 encryption and decryption operations.
 * The key lives exclusively in the Android Keystore — never in memory or disk.
 */
interface CryptoManager {
    /**
     * Encrypts [plainBytes] and returns the encrypted bytes.
     * The IV is prepended to the result for later decryption.
     */
    fun encrypt(plainBytes: ByteArray): ByteArray

    /**
     * Decrypts [encryptedBytes] previously encrypted by [encrypt].
     * Extracts the IV from the first [IV_SIZE] bytes automatically.
     */
    fun decrypt(encryptedBytes: ByteArray): ByteArray

    companion object {
        const val IV_SIZE = 12 // GCM standard IV size in bytes
    }
}
