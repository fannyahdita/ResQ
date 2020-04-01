package com.tugasakhir.resq.rescuer.model

class Posko (
    val idRescuer : String = "",
    val latitude : Long = 0,
    val longitude : Long = 0,
    val poskoName : String = "",
    val city : String = "",
    val district : String = "",
    val subDistrict : String = "",
    val address : String = "",
    val capacity : Long  = 0,
    val hasMedic : Boolean = false,
    val hasKitchen : Boolean = false,
    val hasWC : Boolean = false,
    val hasLogistic : Boolean = false,
    val hasBed : Boolean = false,
    val createdAt : String = "",
    val isOpen : Boolean = true
)