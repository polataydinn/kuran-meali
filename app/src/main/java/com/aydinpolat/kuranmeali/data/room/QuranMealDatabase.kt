package com.aydinpolat.kuranmeali.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aydinpolat.kuranmeali.data.models.Suras

@Database(entities = [Suras::class], version = 1, exportSchema = false)
@TypeConverters(com.aydinpolat.kuranmeali.data.room.TypeConverters::class)
abstract class QuranMealDatabase: RoomDatabase()  {

    abstract fun quranMealDao(): QuranMealDao

    companion object {
        @Volatile
        private var INSTANCE: QuranMealDatabase? = null

        fun getDatabase(context: Context): QuranMealDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranMealDatabase::class.java,
                    "quran_meal_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}