package ng.gov.imostate.model

import com.squareup.moshi.Json

data class Data (
    @Json(name = "name")
    val name: String,
    @Json(name = "vrn")
    val vrn: String,
    @Json(name = "lpd")
    val lpd: String,
    @Json(name = "ob")
    val ob: Long
)