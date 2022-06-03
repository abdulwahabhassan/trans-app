package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Data (
    @Json(name = "vid")
    val vid: String = "",
    @Json(name = "lpd")
    val lpd: String = "",
    @Json(name = "vc")
    val vc: String = "",
    @Json(name = "vpn")
    val vpn: String = ""
)