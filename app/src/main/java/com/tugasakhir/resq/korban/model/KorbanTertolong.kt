package com.tugasakhir.resq.korban.model

import java.io.Serializable

data class KorbanTertolong(
    val idRescuer: String = "",
    val idInfoKorban: String = "",
    val isAccepted: Boolean = false,
    val isOnTheWay: Boolean = false,
    val isRescuerArrived: Boolean = false,
    val isFinished: Boolean = false,
    val date : String = ""
) : Serializable