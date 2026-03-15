package com.docvault.test.utils.fake

import androidx.navigation.NavController
import com.docvault.core.navigation.NavigationCommand
import com.docvault.core.navigation.Navigator

class FakeNavigator : Navigator {
    override fun navigate(command: NavigationCommand) = Unit
    override fun bind(navController: NavController) = Unit
    override fun unbind() = Unit
}