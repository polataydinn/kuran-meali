package com.aydinpolat.kuranmeali.data.room

import androidx.room.TypeConverter
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverters {
    @TypeConverter
    fun fromSuras(ayats: List<Ayats>): String = Gson().toJson(ayats)

    val itemType = object : TypeToken<List<Ayats>>(){}.type
    @TypeConverter
    fun toSuras(ayats: String): List<Ayats> = Gson().fromJson<List<Ayats>>(ayats, itemType)
}