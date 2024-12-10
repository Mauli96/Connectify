package com.example.connectify.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
}