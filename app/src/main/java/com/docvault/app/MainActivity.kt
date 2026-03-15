package com.docvault.app

import androidx.navigation.fragment.NavHostFragment
import com.docvault.app.databinding.ActivityMainBinding
import com.docvault.core.navigation.Navigator
import com.docvault.core.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    @Inject
    lateinit var navigator: Navigator

    private lateinit var navHostFragment: NavHostFragment

    override fun inflateBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initViews() {
        navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navigator.bind(navHostFragment.navController)
    }

    override fun observeState() = Unit

    override fun onSupportNavigateUp(): Boolean = navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()

    override fun onDestroy() {
        super.onDestroy()
        navigator.unbind()
    }
}
