package com.aydinpolat.kuranmeali.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayat_note")
data class UserNote(
    val userMail: String,
    @PrimaryKey
    val userNote: String,
    val ayatId: Int,
    val suraId: Int,
)
