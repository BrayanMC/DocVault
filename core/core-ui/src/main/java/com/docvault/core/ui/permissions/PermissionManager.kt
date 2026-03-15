package com.docvault.core.ui.permissions

/**
 * Manages runtime permission requests and checks.
 * Handles single and multiple permission requests with granted/denied callbacks.
 */
interface PermissionManager {
    fun request(
        permissions: List<String>,
        onGranted: () -> Unit,
        onDenied: () -> Unit = {},
    )
}
