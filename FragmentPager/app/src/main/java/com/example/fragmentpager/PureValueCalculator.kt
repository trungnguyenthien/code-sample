package com.example.fragmentpager

import android.content.Context

object PureValueCalculator {
    fun calculate(start: Double,  end: Double, velocity: Double, current: () -> Double, ) = when (start < end) {
        true -> (current.invoke() + velocity).inRange(start, end).toDouble()
        false -> (current.invoke() + velocity).inRange(end, start).toDouble()
    }
}

fun Context.dp2px(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Number.inRange(min: Number, max: Number) = when {
    (this.toDouble() <= min.toDouble()) -> min
    (this.toDouble() >= max.toDouble()) -> max
    else -> this
}

fun Number.dp2px(context: Context): Number {
    return (this.toFloat() * context.resources.displayMetrics.density)
}