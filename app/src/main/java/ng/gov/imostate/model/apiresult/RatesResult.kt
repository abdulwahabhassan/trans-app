package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.Rate

data class RatesResult (
    val rate: List<Rate>?
        )