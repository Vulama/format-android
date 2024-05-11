package com.format.common.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
@JvmInline
value class Epoch(val value: Long) {

    val formattedAsDate: String
        get() = this.format("dd MMM yyyy")

    fun format(format: String): String = format(DateTimeFormatter.ofPattern(format))

    private fun format(formatter: DateTimeFormatter) = if (this != None) {
        try {
            formatter.format(
                Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault())
            )
        } catch (_: Exception) {
            ""
        }
    } else {
        ""
    }

    operator fun compareTo(epoch: Epoch) = (this.value - epoch.value).toInt()

    operator fun plus(epoch: Epoch): Epoch = Epoch(this.value + epoch.value)

    operator fun minus(epoch: Epoch): Epoch = Epoch(this.value - epoch.value)

    val asLocalDate: LocalDate
        get() = try {
            Instant.ofEpochSecond(value).atZone(ZoneId.systemDefault()).toLocalDate()
        } catch (_: Exception) {
            LocalDate.MIN
        }

    override fun toString() = Instant.ofEpochSecond(value).toString()

    companion object {
        val None = Epoch(0L)
    }
}

fun Long.toEpoch(): Epoch = Epoch(this)

fun String.toEpoch(): Epoch {
    var epoch = try {
        ZonedDateTime
            .parse(this, DateTimeFormatter.ISO_DATE_TIME)
            .toEpochSecond()
            .toEpoch()
    } catch (_: Exception) {
        Epoch.None
    }

    if (epoch.value == Epoch.None.value) {
        epoch = try {
            LocalDateTime
                .parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toEpochSecond(ZoneOffset.UTC)
                .toEpoch()
        } catch (_: Exception) {
            Epoch.None
        }
    }

    if (epoch.value == Epoch.None.value) {
        epoch = try {
            LocalDate
                .parse(this, DateTimeFormatter.ISO_DATE)
                .atStartOfDay()
                .toEpochSecond(ZoneOffset.UTC)
                .toEpoch()
        } catch (_: Exception) {
            Epoch.None
        }
    }

    return epoch
}
