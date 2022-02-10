package com.aydinpolat.kuranmeali.util

import android.app.Activity
import android.media.MediaPlayer
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.models.Suras
import com.google.android.exoplayer2.ExoPlayer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner,object: Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun ExoPlayer.milliSecondsToTimer(ms: Long): String {
    var finalString = ""
    var secondString = ""

    val hours = (ms / (1000 * 60 * 60))
    val minutes = (ms % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (((ms + 500) % (1000 * 60 * 60)) % (1000 * 60) / 1000)

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


fun Activity.hideSoftKeyboard(editText: EditText) {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun List<Ayats>.getItemPositionByName(mAyatId: String): Int {
    this.forEachIndexed { index, it ->
        if (it.ayatId == mAyatId)
            return index
    }
    return 0
}