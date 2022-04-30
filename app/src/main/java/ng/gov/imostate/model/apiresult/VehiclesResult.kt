package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.Vehicle

data class VehiclesResult(
    val vehicle: List<Vehicle>?
)
