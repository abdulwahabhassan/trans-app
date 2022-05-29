package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Setting(
    @Json(name = "setting_key")
    val setting_key: String?,
    val title: String?,
    val type: String?,
    val value: String?,
    val options: List<String>?

)
