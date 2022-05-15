package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.apiresult.FundWalletAccountDetailsResult
import ng.gov.imostate.model.request.FundWalletAccountDetailsRequest
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class FundWalletFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(userPreferencesRepository) {

    suspend fun getFundWalletAccountDetails(token: String, fundWalletAccountDetailsRequest: FundWalletAccountDetailsRequest): ViewModelResult<FundWalletAccountDetailsResult?> {
        val response = agentRepository.getFundWalletAccountDetails(token, fundWalletAccountDetailsRequest)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

}