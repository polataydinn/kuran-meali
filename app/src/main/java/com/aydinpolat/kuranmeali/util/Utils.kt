package com.aydinpolat.kuranmeali.util

import android.media.MediaPlayer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner,object: Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun MediaPlayer.milliSecondsToTimer(ms: Long): String {
    var finalString = ""
    var secondString = ""

    val hours = (ms / (1000 * 60 * 60))
    val minutes = (ms % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = ((ms % (1000 * 60 * 60)) % (1000 * 60) / 1000)

    if (hours > 0) {
        secondString = "$hours"
    }

    if (seconds < 10) {
        secondString = "0" + seconds
    } else {
        secondString = "" + seconds
    }

    finalString = finalString + minutes + ":" + secondString
    return finalString
}