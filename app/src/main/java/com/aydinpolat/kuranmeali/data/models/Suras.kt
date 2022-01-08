package com.aydinpolat.kuranmeali.data.models

data class Suras(
    val suraName: String,
    val suraId: Int,
    var suraNote: String,
    val ayets: List<Ayats>
)