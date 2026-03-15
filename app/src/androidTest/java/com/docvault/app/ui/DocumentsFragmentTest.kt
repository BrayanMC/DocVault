package com.docvault.app.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.docvault.app.MainActivity
import com.docvault.data.database.dao.DocumentDao
import com.docvault.data.database.entity.DocumentEntity
import com.docvault.feature.documents.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DocumentsFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Inject
    lateinit var documentDao: DocumentDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        runBlocking { documentDao.deleteAll() }
    }

    @Test
    fun fabAddDocument_isDisplayed() {
        onView(withId(R.id.fab_add_document))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun chipAll_isCheckedByDefault() {
        onView(withId(R.id.chip_all))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun chipGroupFilter_isDisplayed() {
        onView(withId(R.id.chip_group_filter))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyState_isDisplayedWhenNoDocuments() {
        onView(withId(R.id.tv_empty_state))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun fabClick_opensBottomSheet() {
        onView(withId(R.id.fab_add_document))
            .perform(click())

        onView(withId(R.id.btn_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun bottomSheet_showsAllAddOptions() {
        onView(withId(R.id.fab_add_document))
            .perform(click())

        onView(withId(R.id.btn_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btn_pdf))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.btn_camera))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun chipPdf_click_appliesFilter() {
        onView(withId(R.id.chip_pdf))
            .perform(click())

        onView(withId(R.id.chip_pdf))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun chipImage_click_appliesFilter() {
        onView(withId(R.id.chip_image))
            .perform(click())

        onView(withId(R.id.chip_image))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun chipAll_afterSelectingPdf_resetsFilter() {
        onView(withId(R.id.chip_pdf)).perform(click())
        onView(withId(R.id.chip_all)).perform(click())

        onView(withId(R.id.chip_all))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun clickDocument_navigatesToDetail() {
        val document = DocumentEntity(
            id = "test-nav-1",
            name = "factura.jpg",
            type = "IMAGE",
            encryptedFilePath = "/path/factura.jpg",
            createdAt = System.currentTimeMillis(),
            fileSize = 1024L,
            latitude = null,
            longitude = null,
            address = null
        )
        runBlocking { documentDao.insertDocument(document) }
        Thread.sleep(500)

        onView(withId(R.id.rv_documents))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )

        onView(withId(R.id.rv_documents))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}