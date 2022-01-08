package com.aydinpolat.kuranmeali.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.repository.Repository
import com.aydinpolat.kuranmeali.data.room.QuranMealDao
import com.aydinpolat.kuranmeali.data.room.QuranMealDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseViewModel(application: Application): AndroidViewModel(application) {

    private val quranMealDao = QuranMealDatabase.getDatabase(application).quranMealDao()
    val repository = Repository(quranMealDao)
    val getAyats = repository.getAllAyats

    fun insertAyat(ayats: Ayats){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAyat(ayats)
        }
    }
}