package com.aydinpolat.kuranmeali.data.repository

import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.room.QuranMealDao

class Repository(private val quranMealDao: QuranMealDao) {

    val getAllAyats = quranMealDao.getAllAyats()

    suspend fun insertAyat(ayats: Ayats){
        quranMealDao.insertAyat(ayats)
    }
}