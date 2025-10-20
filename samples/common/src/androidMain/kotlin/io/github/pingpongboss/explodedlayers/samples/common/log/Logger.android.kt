package io.github.pingpongboss.explodedlayers.samples.common.log

import android.util.Log

actual object Logger {

    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
