package com.docvault.core.navigation

sealed class NavigationCommand {
    data class ToDetail(val documentId: String) : NavigationCommand()

    data object Back : NavigationCommand()
}

object NavigationArgs {
    const val ARG_DOCUMENT_ID = "document_id"
}
