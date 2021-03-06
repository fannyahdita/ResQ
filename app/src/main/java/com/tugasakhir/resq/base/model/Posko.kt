package com.tugasakhir.resq.base.model

import java.io.Serializable

class Posko (
    val id : String = "",
    val idRescuer : String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val poskoName : String = "",
    val mapAddress : String = "",
    val noteAddress : String = "",
    val capacity : Long  = 0,
    val hasMedic : Boolean = false,
    val hasKitchen : Boolean = false,
    val hasWC : Boolean = false,
    val hasLogistic : Boolean = false,
    val hasBed : Boolean = false,
    val additionalInfo : String = "",
    val createdAt : String = "",
    val contactName : String = "",
    val contactNumber : String = "",
    val isOpen : Boolean = true
) : Serializable