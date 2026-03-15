package com.docvault.lib.security.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class CryptoManagerImpl @Inject constructor() : CryptoManager {
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "docvault_master_key"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEY_SIZE = 256
        private const val GCM_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore =
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }

    override fun encrypt(plainBytes: ByteArray): ByteArray {
        val cipher =
            getCipher().apply {
                init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            }
        val encryptedBytes = cipher.doFinal(plainBytes)
        return cipher.iv + encryptedBytes
    }

    override fun decrypt(encryptedBytes: ByteArray): ByteArray {
        val iv = encryptedBytes.copyOfRange(0, CryptoManager.IV_SIZE)
        val ciphertext = encryptedBytes.copyOfRange(CryptoManager.IV_SIZE, encryptedBytes.size)
        val cipher =
            getCipher().apply {
                init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
            }
        return cipher.doFinal(ciphertext)
    }

    private fun getCipher(): Cipher = Cipher.getInstance(TRANSFORMATION)

    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateKey()
    }

    private fun generateKey(): SecretKey {
        val keyGenSpec =
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(KEY_SIZE)
                .setUserAuthenticationRequired(false)
                .build()

        return KeyGenerator.getInstance(ALGORITHM, KEYSTORE_PROVIDER).apply {
            init(keyGenSpec)
        }.generateKey()
    }
}
