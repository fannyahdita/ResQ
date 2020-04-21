package com.tugasakhir.resq.korban.model

import java.io.Serializable

data class AkunKorban(
    val name: String = "",
    val phone: String = "",
    val profilePhoto: String = "",
    val isAskingHelp: Boolean = false
) : Serializable