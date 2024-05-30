package com.format.data.infrastructure.logger

import android.util.Log
import com.format.common.infrastructure.logger.Logger
import com.format.data.infrastructure.dateTime.DateTimeProvider
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class ForMatLogger(
    private val dateTimeProvider: DateTimeProvider,
) : Logger {
    private val loggingOrder = LoggingOrder()

    override fun v(tag: String, message: String?, vararg args: Any?) {
        log(tag, Log.VERBOSE, null, message, args)
    }

    override fun d(tag: String, message: String?, vararg args: Any?) {
        log(tag, Log.DEBUG, null, message, args)
    }

    override fun i(tag: String, message: String?, vararg args: Any?) {
        log(tag, Log.INFO, null, message, args)
    }

    override fun w(tag: String, message: String?, vararg args: Any?) {
        log(tag, Log.WARN, null, message, args)
    }

    override fun e(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
        log(tag, Log.ERROR, t, message, args)
    }

    private fun log(
        tag: String,
        priority: Int,
        t: Throwable?,
        message: String?,
        vararg args: Any?
    ) {
        if (priority < Log.INFO || message.isNullOrEmpty()) {
            return
        }

        Timber.log(priority, format(tag, message), t, normalizeArgs(*args) + defaultArgs())
    }

    private fun normalizeArgs(vararg args: Any?): Map<String, Any?> =
        args.filterIsInstance<Pair<String, Any?>>().toMap()

    private fun defaultArgs() = mapOf(
        "execution_order" to loggingOrder.incrementAndGet(),
        "device_timestamp" to dateTimeProvider.now().formattedAsApiDate
    )

    private fun format(tag: String, message: String) = "$tag: $message"
}

private class LoggingOrder {
    private val atomicCounter = AtomicInteger(0)
    private val lock = ReentrantLock()

    fun incrementAndGet(): Int {
        lock.lock()
        try {
            return atomicCounter.incrementAndGet()
        } finally {
            lock.unlock()
        }
    }
}