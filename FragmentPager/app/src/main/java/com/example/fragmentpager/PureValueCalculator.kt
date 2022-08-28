package com.example.fragmentpager

import android.content.Context
import android.util.Log
import kotlin.math.abs

object PureValueCalculator {
    fun calculate(
        startValue: Double,
        endValue: Double,
        velocity: Double,
        currentValue: () -> Double,
    ): Double {
        val current = currentValue.invoke()
        val distance = abs(endValue - startValue)
        var newValue = current
        if (startValue < endValue) {
            newValue += velocity
            if (endValue <= newValue) {
                newValue = endValue
            } else if (startValue >= newValue) {
                newValue = startValue
            }
        } else {
            newValue += velocity
            if (endValue >= newValue) {
                newValue = endValue
            } else if (startValue <= newValue) {
                newValue = startValue
            }
        }
        Log.w("MY", "PureValueCalculator = " + newValue)
        return newValue
    }
}

fun Context.dp2px(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Number.dp2px(context: Context): Number {
    return (this.toFloat() * context.resources.displayMetrics.density)
}