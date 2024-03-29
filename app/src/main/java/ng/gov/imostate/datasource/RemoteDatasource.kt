package ng.gov.imostate.datasource

import ng.gov.imostate.api.ApiService
import ng.gov.imostate.model.apiresult.*
import ng.gov.imostate.model.request.*
import ng.gov.imostate.model.response.*
import javax.inject.Inject

class RemoteDatasource  @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun loginAgent(loginRequest: LoginRequest): ApiResponse<LoginResult> {
        return apiService.login(loginRequest = loginRequest)
    }

    suspend fun onboardVehicle(token: String, onboardVehicleRequest: OnboardVehicleRequest):
            ApiResponse<OnboardVehicleResult> {
        return apiService.onboardVehicle(token, onboardVehicleRequest)
    }

    suspend fun getAllVehiclesFromCurrentEnumeration(token: String,): ApiResponse<VehiclesResult> {
        return apiService.getAllVehiclesFromCurrentEnumeration(token)
    }

    suspend fun getAllVehiclesFromPreviousEnumeration(token: String, lastVehicleId: Long): ApiResponse<VehiclesResult> {
        return apiService.getAllVehiclesFromPreviousEnumeration(token, lastVehicleId)
    }

    suspend fun getVehicle(token: String, vehicleId: String): ApiResponse<VehicleResult> {
        return apiService.getVehicle(token, vehicleId)
    }

    suspend fun getDashBoardMetrics(token: String,): ApiResponse<MetricsResult> {
        return apiService.getDashboardMetrics(token)
    }

    suspend fun createSyncTransactions(
        token: String,
        data: CreateSyncTransactionsRequest)
    : ApiResponse<SyncTransactionsResult> {
        return apiService.createSyncTransactions(token, data = data)
    }

    suspend fun getTransactions(token: String,): ApiResponse<TransactionsResult> {
        return apiService.getTransactions(token)
    }

    suspend fun getRates(token: String): ApiResponse<RatesResult> {
        return apiService.getRates(token)
    }

    suspend fun getFundWalletAccountDetails(
        token: String,
        fundWalletAccountDetailsRequest: FundWalletAccountDetailsRequest
    ): ApiResponse<FundWalletAccountDetailsResult> {
        return apiService.getFundWalletAccountDetails(token, fundWalletAccountDetailsRequest)
    }

    suspend fun getCurrentUser(
        token: String
    ): ApiResponse<CurrentUserResult> {
        return apiService.getCurrentUser(token)
    }

}
