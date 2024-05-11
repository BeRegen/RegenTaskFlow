package org.tasks.time

import android.annotation.SuppressLint
import org.tasks.BuildConfig
import org.tasks.date.DateTimeUtils.toDateTime

object DateTimeUtils {
    @SuppressLint("DefaultLocale")
    fun printDuration(millis: Long): String = if (BuildConfig.DEBUG) {
        val seconds = millis / 1000
        String.format(
                "%dh %dm %ds", seconds / 3600L, (seconds % 3600L / 60L).toInt(), (seconds % 60L).toInt())
    } else millis.toString()

    @JvmStatic
    fun Long.startOfDay(): Long = if (this > 0) toDateTime().startOfDay().millis else 0

    fun Long.startOfMinute(): Long = if (this > 0) toDateTime().startOfMinute().millis else 0

    fun Long.startOfSecond(): Long = if (this > 0) toDateTime().startOfSecond().millis else 0

    fun Long.millisOfDay(): Int = if (this > 0) toDateTime().millisOfDay else 0

    fun Long.toDate(): net.fortuna.ical4j.model.Date? = this.toDateTime().toDate()

    fun Long.withMillisOfDay(millisOfDay: Int): Long =
        if (this > 0) toDateTime().withMillisOfDay(millisOfDay).millis else 0
}