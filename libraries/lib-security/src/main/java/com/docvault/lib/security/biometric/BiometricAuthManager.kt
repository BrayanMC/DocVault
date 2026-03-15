package com.docvault.lib.security.biometric

import androidx.fragment.app.FragmentActivity

/**
 * Contract for biometric authentication operations.
 */
interface BiometricAuthManager {
    /**
     * Launches a biometric prompt on the given [activity].
     * Calls [onSuccess] if authentication passes, [onError] otherwise.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (errorCode: Int, message: String) -> Unit,
    )
}
