package com.docvault.app.utils

import android.R
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

inline fun <reified F : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    crossinline onActivity: (HiltTestActivity) -> Unit = {},
): ActivityScenario<HiltTestActivity> {
    val intent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    )
    return ActivityScenario.launch<HiltTestActivity>(intent).also { scenario ->
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragmentFactory
                .instantiate(F::class.java.classLoader!!, F::class.java.name)
            fragment.arguments = fragmentArgs
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commitNow()
            onActivity(activity)
        }
    }
}