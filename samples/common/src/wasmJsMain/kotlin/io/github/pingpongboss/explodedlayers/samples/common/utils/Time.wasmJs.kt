package io.github.pingpongboss.explodedlayers.samples.common.utils

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => Date.now()")
private external fun jsDateNow(): Double

actual fun currentTimeMillis(): Long = jsDateNow().toLong()
