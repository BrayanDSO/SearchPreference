package com.bytehamster.lib.preferencesearch.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewAnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.sqrt

object AnimationUtils {
    fun registerCircularRevealAnimation(
        context: Context,
        view: View,
        revealSettings: RevealAnimationSetting,
    ) {
        val startColor = revealSettings.colorAccent
        val endColor = getBackgroundColor(view)

        view.addOnLayoutChangeListener(
            object : OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int,
                ) {
                    v.removeOnLayoutChangeListener(this)
                    view.visibility = View.VISIBLE
                    val cx = revealSettings.centerX
                    val cy = revealSettings.centerY
                    val width = revealSettings.width
                    val height = revealSettings.height
                    val duration = context.resources.getInteger(android.R.integer.config_longAnimTime)

                    // Simply use the diagonal of the view
                    val finalRadius = sqrt((width * width + height * height).toDouble()).toFloat()
                    val anim =
                        ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                            .setDuration(duration.toLong())
                    anim.interpolator = FastOutSlowInInterpolator()
                    anim.start()
                    startColorAnimation(view, startColor, endColor, duration)
                }
            },
        )
    }

    private fun startColorAnimation(
        view: View,
        startColor: Int,
        endColor: Int,
        duration: Int,
    ) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator ->
            view.setBackgroundColor(
                (valueAnimator.animatedValue as Int),
            )
        }
        anim.setDuration(duration.toLong())
        anim.start()
    }

    private fun getBackgroundColor(view: View): Int {
        var color = Color.TRANSPARENT
        val background = view.background
        if (background is ColorDrawable) {
            color = background.color
        }
        return color
    }
}
