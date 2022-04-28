package ng.gov.imostate.datasource

import ng.gov.imostate.api.ApiService
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.response.*
import javax.inject.Inject

class RemoteDatasource  @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun loginAgent(loginRequest: LoginRequest): ApiResponse<LoginResult> {
        return apiService.login(loginRequest = loginRequest)
    }

    suspend fun onboardVehicle(token: String, onboardVehicleRequest: OnboardVehicleRequest): ApiResponse<OnboardVehicleResult> {
        return apiService.onboardVehicle(token, onboardVehicleRequest)
    }

    suspend fun getAllVehicles(token: String,): ApiResponse<VehiclesResult> {
        return apiService.getAllVehicles(token)
    }

    suspend fun getDashBoardMetrics(token: String,): ApiResponse<MetricsResult> {
        return apiService.getDashboardMetrics(token)
    }

    suspend fun createSyncTransactions(token: String, vehicleId: Int, data: CreateSyncTransactionsRequest): ApiResponse<SyncTransactionsResult> {
        return apiService.createSyncTransactions(token, vehicleId = vehicleId, data = data)
    }

    suspend fun getTransactions(token: String,): ApiResponse<TransactionsResult> {
        return apiService.getTransactions(token)
    }
}
