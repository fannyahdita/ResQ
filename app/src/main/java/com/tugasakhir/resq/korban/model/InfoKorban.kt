package com.tugasakhir.resq.korban.model

data class InfoKorban(
    val idKorban: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val jumlahLansia: Int = 0,
    val jumlahDewasa: Int = 0,
    val jumlahAnak: Int = 0,
    val infoTambahan: String = "",
    val bantuanMakanan: Boolean = false,
    val bantuanMedis: Boolean = false,
    val bantuanEvakuasi: Boolean = false
)