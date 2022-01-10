package com.aydinpolat.kuranmeali.data.repository

import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.room.QuranMealDao

class Repository(private val quranMealDao: QuranMealDao) {

    val getAllSuras = quranMealDao.getAllSuras()

    suspend fun insertSura(suras: Suras){
        quranMealDao.insertSura(suras)
    }
}