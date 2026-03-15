package com.docvault.app.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.docvault.core.navigation.NavigationArgs
import com.docvault.data.database.dao.DocumentDao
import com.docvault.app.utils.launchFragmentInHiltContainer
import com.docvault.data.database.entity.DocumentEntity
import com.docvault.feature.detail.R
import com.docvault.feature.detail.presentation.ui.DetailFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DetailFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var documentDao: DocumentDao

    private val fakeDocument = DocumentEntity(
        id = "detail-test-1",
        name = "contrato.jpg",
        type = "IMAGE",
        encryptedFilePath = "/path/contrato.jpg",
        createdAt = System.currentTimeMillis(),
        fileSize = 2048L,
        latitude = -12.0,
        longitude = -77.0,
        address = "Av. Test 123, Lima"
    )

    @Before
    fun setup() {
        hiltRule.inject()
        runBlocking { documentDao.insertDocument(fakeDocument) }
    }

    @After
    fun tearDown() {
        runBlocking { documentDao.deleteAll() }
    }

    private fun launchDetail() {
        val args = Bundle().apply {
            putString(NavigationArgs.ARG_DOCUMENT_ID, fakeDocument.id)
        }
        launchFragmentInHiltContainer<DetailFragment>(args)
        Thread.sleep(1000)
    }

    @Test
    fun detailFragment_withFakeBiometric_showsContentView() {
        launchDetail()
        Espresso.onView(ViewMatchers.withId(R.id.content_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun detailFragment_withFakeBiometric_showsDocumentName() {
        launchDetail()
        Espresso.onView(ViewMatchers.withId(R.id.tv_document_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun detailFragment_withFakeBiometric_showsDeleteButton() {
        launchDetail()
        Espresso.onView(ViewMatchers.withId(R.id.btn_delete))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun detailFragment_hasSecureWindowFlag() {
        val args = Bundle().apply {
            putString(NavigationArgs.ARG_DOCUMENT_ID, fakeDocument.id)
        }

        launchFragmentInHiltContainer<DetailFragment>(args) { activity ->
            val flags = activity.window.attributes.flags
            assertTrue(flags and WindowManager.LayoutParams.FLAG_SECURE != 0)
        }
    }
}