@file:JvmName("BrazeResults")

package com.tealium.remotecommands.braze

/**
 * Returns the failure carried by a [Result] that the Braze SDK hands to a callback, or null when
 * it succeeded.
 *
 * Java cannot call the accessors of [Result] itself: it is a value class, so any function taking
 * or returning it gets a mangled JVM name. The parameter is typed [Any] for the same reason, and
 * the callback's boxed [Result] arrives as one.
 */
internal fun failureOrNull(result: Any?): Throwable? = (result as? Result<*>)?.exceptionOrNull()
