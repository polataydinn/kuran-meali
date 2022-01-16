package com.aydinpolat.kuranmeali.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.models.UserNote

@Dao
interface QuranMealDao {
    @Query("Select * From sura_names")
    fun getAllSuras(): LiveData<List<Suras>>?

    @Query("Select * From ayat_note")
    fun getAllNotes(): LiveData<List<UserNote>>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    suspend fun insertSura(suras: Suras)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(userNote: UserNote)

    @Query("SELECT * FROM sura_names WHERE suraName LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<Suras>>

    @Query("SELECT ayets FROM sura_names WHERE ayets LIKE :searchQuery")
    fun searchDatabaseForAyat(searchQuery: String): LiveData<List<Ayats>>
    
}