package com.aydinpolat.kuranmeali.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aydinpolat.kuranmeali.data.models.Ayats

@Database(entities = [Ayats::class], version = 1, exportSchema = false)
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