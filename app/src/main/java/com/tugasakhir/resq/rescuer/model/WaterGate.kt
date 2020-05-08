package com.tugasakhir.resq.rescuer.model

import java.io.Serializable

data class WaterGate(
    val name : String,
    val location : String,
    val latitude : Double,
    val longitude : Double,
    val datetime : String,
    val level : Long,
    val status : String
) : Serializable