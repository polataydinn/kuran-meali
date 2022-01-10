package com.aydinpolat.kuranmeali.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sura_names")
data class Suras(
    val suraName: String,
    @PrimaryKey
    val suraId: Int,
    var suraNote: String,
    val ayets: List<Ayats>,
    var ayetsArabic: List<Ayats>
)