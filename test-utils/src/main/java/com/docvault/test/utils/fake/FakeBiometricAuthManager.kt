package com.docvault.test.utils.fake

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.docvault.core.navigation.NavigationCommand
import com.docvault.core.navigation.Navigator
import com.docvault.lib.security.biometric.BiometricAuthManager

class FakeBiometricAuthManager : BiometricAuthManager {
    override fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (errorCode: Int, message: String) -> Unit,
    ) {
        onSuccess()
    }
}