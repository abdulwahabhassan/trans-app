package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Data (
    @Json(name = "name")
    val name: String,
    @Json(name = "vid")
    val vid: String,
    @Json(name = "lpd")
    val lpd: String,
    @Json(name = "ob")
    val ob: Double,
    @Json(name = "vpn")
    val vpn: String
)