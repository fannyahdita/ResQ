package com.tugasakhir.resq.rescuer.model

import java.io.Serializable

class Posko (
    val idRescuer : String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val poskoName : String = "",
    val city : String = "",
    val district : String = "",
    val subDistrict : String = "",
    val mapAddress : String = "",
    val noteAddress : String = "",
    val capacity : Long  = 0,
    val hasMedic : Boolean = false,
    val hasKitchen : Boolean = false,
    val hasWC : Boolean = false,
    val hasLogistic : Boolean = false,
    val hasBed : Boolean = false,
    val createdAt : String = "",
    val contactName : String = "",
    val contactNumber : String = "",
    val isOpen : Boolean = true
) : Serializable