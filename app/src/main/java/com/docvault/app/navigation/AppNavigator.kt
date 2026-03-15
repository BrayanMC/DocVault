package com.docvault.app.navigation

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import com.docvault.app.R
import com.docvault.core.navigation.NavigationArgs
import com.docvault.core.navigation.NavigationCommand
import com.docvault.core.navigation.Navigator
import javax.inject.Inject

class AppNavigator @Inject constructor() : Navigator {
    private var navController: NavController? = null

    override fun bind(navController: NavController) {
        this.navController = navController
    }

    override fun unbind() {
        navController = null
    }

    override fun navigate(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.ToDetail -> navigateToDetail(command.documentId)
            NavigationCommand.Back -> navController?.navigateUp()
        }
    }

    private fun navigateToDetail(documentId: String) {
        Log.d(
            "AppNavigator",
            "navigateToDetail: $documentId, navController: $navController"
        )
        val bundle =
            Bundle().apply {
                putString(NavigationArgs.ARG_DOCUMENT_ID, documentId)
            }
        navController?.navigate(R.id.detailFragment, bundle)
    }
}
