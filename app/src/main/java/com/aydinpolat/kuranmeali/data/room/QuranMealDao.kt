package com.aydinpolat.kuranmeali.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aydinpolat.kuranmeali.data.models.Ayats

@Dao
interface QuranMealDao {
    @Query("Select * From ayats_name")
    fun getAllAyats(): List<Ayats>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAyat(ayats: Ayats)
}