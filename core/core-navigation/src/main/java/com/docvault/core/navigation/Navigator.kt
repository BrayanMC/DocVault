package com.docvault.core.navigation

import androidx.navigation.NavController

/**
 * Contract for navigation operations across features.
 * Features depend on this interface — never on each other directly.
 */
interface Navigator {
    fun navigate(command: NavigationCommand)

    fun bind(navController: NavController)

    fun unbind()
}
