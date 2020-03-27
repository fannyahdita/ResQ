package com.tugasakhir.resq.korban.model

data class KorbanTertolong(
    val idRescuer: String = "",
    val idKorban: String = "",
    val isAccepted: Boolean = false,
    val isOntheWay: Boolean = false,
    val isFinished: Boolean = false
)