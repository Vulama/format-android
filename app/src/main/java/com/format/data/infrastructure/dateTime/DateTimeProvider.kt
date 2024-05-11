package com.format.data.infrastructure.dateTime

import com.format.common.model.Epoch
import java.time.ZonedDateTime
import java.util.TimeZone

interface DateTimeProvider {
    fun now(): Epoch

    fun timeZoneId(): String

    class Default(
        private val timeZone: TimeZone,
    ) : DateTimeProvider {

        override fun now(): Epoch = Epoch(ZonedDateTime.now().toEpochSecond())

        override fun timeZoneId(): String = timeZone.id
    }
}