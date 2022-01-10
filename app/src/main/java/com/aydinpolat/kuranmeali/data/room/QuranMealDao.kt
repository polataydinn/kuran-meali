package com.aydinpolat.kuranmeali.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.models.Suras

@Dao
interface QuranMealDao {
    @Query("Select * From sura_names")
    fun getAllSuras(): LiveData<List<Suras>>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    suspend fun insertSura(suras: Suras)
}