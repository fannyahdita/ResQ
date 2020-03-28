package com.tugasakhir.resq.korban.model

data class KorbanTertolong(
    val idRescuer: String = "",
    val idInfoKorban: String = "",
    val isAccepted: Boolean = false,
    val isOnTheWay: Boolean = false,
    val isFinished: Boolean = false
)