package ng.gov.imostate.model.apiresult

import com.squareup.moshi.Json
import ng.gov.imostate.model.domain.Vehicle

data class OnboardVehicleResult(
    @Json(name = "vehicle")
    val onBoardVehicle: Vehicle?
)
