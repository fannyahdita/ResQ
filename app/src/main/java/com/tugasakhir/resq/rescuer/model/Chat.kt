package com.tugasakhir.resq.rescuer.model

data class Chat(
    val id: String = "",
    val text: String = "",
    val fromId: String = "",
    val toId: String = "",
    val timestamp: Long = 0
) {
}