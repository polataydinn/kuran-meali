package com.aydinpolat.kuranmeali.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayats_name")
data class Ayats(
    val ayatText: String,
    val suraName: String,
    @PrimaryKey
    val ayatId: String,
    var bkz: String,
    var ayatNote: String
)
