package com.rcraggs.doubledose.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import com.getkeepsafe.taptargetview.TapTarget
import com.rcraggs.doubledose.R
import kotlinx.android.synthetic.main.activity_history.*
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

fun RecyclerView.setUpVerticalFixedWidthWithRule() {

    this.setHasFixedSize(true)

    val mDividerItemDecoration = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
    )
    this.addItemDecoration(mDividerItemDecoration)
}


fun TapTarget.defaultConfig(): TapTarget {
    return this.outerCircleColor(R.color.primary)      // Specify a color for the outer circle
            .outerCircleAlpha(0.80f)            // Specify the alpha amount for the outer circle
            .targetCircleColor(R.color.primary_light)   // Specify a color for the target circle
//                                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
//                                    .titleTextColor(R.color.white)      // Specify the color of the title text
//                                    .descriptionTextSize(10)            // Specify the size (in sp) of the description text
//                                    .descriptionTextColor(R.color.red)  // Specify the color of the description text
            .textColor(R.color.primary_light)            // Specify a color for both the title and description text
//                                    .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
//                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
            .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
//                                    .tintTarget(true)                   // Whether to tint the target view's color
            .targetRadius(40)
}

fun TapTarget.fullScreen(): TapTarget {
    return this.targetRadius(0)
            .targetCircleColor(R.color.primary_light)
            .textColor(R.color.primary_light)
}