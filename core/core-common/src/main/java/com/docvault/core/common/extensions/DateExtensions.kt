package com.docvault.core.common.extensions

import com.docvault.core.common.util.DateFormatter

fun Long.toFormattedDate(): String = DateFormatter.formatFull(this)

fun Long.toFormattedDateOnly(): String = DateFormatter.formatDateOnly(this)

fun Long.toFormattedTimeOnly(): String = DateFormatter.formatTimeOnly(this)
