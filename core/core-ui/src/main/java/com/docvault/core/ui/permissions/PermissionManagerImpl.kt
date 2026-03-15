package com.docvault.core.ui.permissions

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManagerImpl(private val fragment: Fragment) : PermissionManager {
    private var launcher: ActivityResultLauncher<Array<String>>
    private var onGranted: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null

    init {
        launcher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { permissions ->
                if (permissions.values.all { it }) {
                    onGranted?.invoke()
                } else {
                    onDenied?.invoke()
                }
            }
    }

    override fun request(
        permissions: List<String>,
        onGranted: () -> Unit,
        onDenied: () -> Unit,
    ) {
        this.onGranted = onGranted
        this.onDenied = onDenied

        val notGranted =
            permissions.filter {
                ContextCompat.checkSelfPermission(
                    fragment.requireContext(), it,
                ) != PackageManager.PERMISSION_GRANTED
            }

        if (notGranted.isEmpty()) {
            onGranted()
        } else {
            launcher.launch(notGranted.toTypedArray())
        }
    }
}
