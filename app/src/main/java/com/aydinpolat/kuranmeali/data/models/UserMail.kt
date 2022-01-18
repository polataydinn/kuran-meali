package com.aydinpolat.kuranmeali.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_email")
data class UserMail(
    @PrimaryKey
    val userEmail: String,
    val userName: String,
    val userSurname: String
)
