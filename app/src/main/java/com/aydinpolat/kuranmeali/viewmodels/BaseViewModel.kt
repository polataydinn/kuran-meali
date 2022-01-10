package com.aydinpolat.kuranmeali.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.repository.Repository
import com.aydinpolat.kuranmeali.data.room.QuranMealDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BaseViewModel(application: Application): AndroidViewModel(application) {

    private val quranMealDao = QuranMealDatabase.getDatabase(application).quranMealDao()
    val repository = Repository(quranMealDao)
    val getAllSuras = repository.getAllSuras

    fun insertSura(suras: Suras){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSura(suras)
        }
    }
}