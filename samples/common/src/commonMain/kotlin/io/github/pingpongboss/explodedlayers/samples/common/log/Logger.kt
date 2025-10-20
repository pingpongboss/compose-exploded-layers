package io.github.pingpongboss.explodedlayers.samples.common.log

expect object Logger {

    fun d(tag: String, message: String)

    fun e(tag: String, message: String, throwable: Throwable? = null)
}
