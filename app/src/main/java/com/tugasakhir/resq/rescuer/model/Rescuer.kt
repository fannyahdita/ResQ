package com.tugasakhir.resq.rescuer.model

import java.io.Serializable

data class Rescuer(
    val name: String = "",
    val email: String = "",
    val profilePhoto : String = "",
    val phone: String = "",
    val instansi: String = "",
    val division: String = "",
    val employeeID: String = "",
    val isHelping: Boolean = false
) : Serializable {

}