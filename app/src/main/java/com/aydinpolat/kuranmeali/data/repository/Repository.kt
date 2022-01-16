package com.aydinpolat.kuranmeali.data.repository

import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.models.UserMail
import com.aydinpolat.kuranmeali.data.models.UserNote
import com.aydinpolat.kuranmeali.data.room.QuranMealDao
import com.aydinpolat.kuranmeali.data.room.TypeConverters

class Repository(private val quranMealDao: QuranMealDao) {
    val typeConverters = TypeConverters()

    val getAllSuras = quranMealDao.getAllSuras()
    val getAllNote = quranMealDao.getAllNotes()
    val getAllUserMails = quranMealDao.getAllUserMails()

    suspend fun insertSura(suras: Suras) {
        quranMealDao.insertSura(suras)
    }

    suspend fun insertUserMail(userMail: UserMail){
        quranMealDao.insertUserMail(userMail)
    }

    suspend fun insertNote(userNote: UserNote) {
        quranMealDao.insertNote(userNote)
    }

    fun searchDatabase(searchQuery: String) = quranMealDao.searchDatabase(searchQuery)
}