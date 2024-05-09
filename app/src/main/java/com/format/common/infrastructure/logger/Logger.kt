package com.format.common.infrastructure.logger

interface Logger {
    fun v(tag: String, message: String?, vararg args: Any?)

    fun d(tag: String, message: String?, vararg args: Any?)

    fun i(tag: String, message: String?, vararg args: Any?)

    fun w(tag: String, message: String?, vararg args: Any?)

    fun e(tag: String, t: Throwable?, message: String?, vararg args: Any?)
}