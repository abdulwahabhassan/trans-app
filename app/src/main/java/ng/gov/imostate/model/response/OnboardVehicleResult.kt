package ng.gov.imostate.model.response

import com.squareup.moshi.Json

data class OnboardVehicleResult(
    @Json(name = "vehicle")
    val onBoardVehicle: OnboardVehicle?
)
