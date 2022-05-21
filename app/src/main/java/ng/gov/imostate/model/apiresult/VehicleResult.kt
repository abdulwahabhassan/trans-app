package ng.gov.imostate.model.apiresult

import com.squareup.moshi.Json
import ng.gov.imostate.model.domain.Rate
import ng.gov.imostate.model.domain.Vehicle

data class VehicleResult (
    val vehicle: Vehicle?
)
