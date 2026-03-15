package com.docvault.core.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionChecker {
    fun isGranted(
        context: Context,
        permission: String,
    ): Boolean =
        ContextCompat.checkSelfPermission(
            context, permission,
        ) == PackageManager.PERMISSION_GRANTED
}
