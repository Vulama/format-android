package com.format.data.infrastructure

import com.format.common.infrastructure.logger.Logger


// todo implement firebase logger
class ForMatLogger : Logger {
    override fun v(tag: String, message: String?, vararg args: Any?) {
        println("V - ${message}")
    }

    override fun d(tag: String, message: String?, vararg args: Any?) {
        println("D - ${message}")
    }

    override fun i(tag: String, message: String?, vararg args: Any?) {
        println("I - ${message}")
    }

    override fun w(tag: String, message: String?, vararg args: Any?) {
        println("W - ${message}")
    }

    override fun e(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
        println("E - ${message}")
    }
}