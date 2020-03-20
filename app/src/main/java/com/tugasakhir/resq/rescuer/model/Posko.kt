package com.tugasakhir.resq.rescuer.model

data class Posko (
    val uidRescuer: String? = "",
    val name : String = "",
    val address : String = "",
    val kotaAdministrasi : String = "",
    val kecamatan : String = "",
    val kelurahan : String = "",
    val latitude : String? = "",
    val longitude : String? = "",
    val hasMedic : Boolean = false,
    val hasKitchen : Boolean = false,
    val hasLogistic : Boolean = false,
    val hasWC : Boolean = false,
    val hasBed : Boolean = false
) {
}