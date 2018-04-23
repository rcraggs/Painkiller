package com.rcraggs.doubledose.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import org.threeten.bp.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val innerObserver = Observer<T> {
        value = it
        latch.countDown()
    }
    observeForever(innerObserver)
    latch.await(2, TimeUnit.SECONDS)
    return value
}


fun Instant.dayAgo(): Instant = this.minusSeconds(60*60*24)