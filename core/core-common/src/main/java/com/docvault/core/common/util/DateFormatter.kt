package com.docvault.core.common.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private const val FORMAT_FULL = "MMM dd, yyyy HH:mm"
    private const val FORMAT_DATE_ONLY = "MMM dd, yyyy"
    private const val FORMAT_TIME_ONLY = "HH:mm"

    private val fullFormatter by lazy {
        SimpleDateFormat(FORMAT_FULL, Locale.getDefault())
    }

    private val dateOnlyFormatter by lazy {
        SimpleDateFormat(FORMAT_DATE_ONLY, Locale.getDefault())
    }

    private val timeOnlyFormatter by lazy {
        SimpleDateFormat(FORMAT_TIME_ONLY, Locale.getDefault())
    }

    fun formatFull(timestamp: Long): String = fullFormatter.format(Date(timestamp))

    fun formatDateOnly(timestamp: Long): String = dateOnlyFormatter.format(Date(timestamp))

    fun formatTimeOnly(timestamp: Long): String = timeOnlyFormatter.format(Date(timestamp))
}
