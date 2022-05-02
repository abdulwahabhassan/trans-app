package ng.gov.imostate.api

import ng.gov.imostate.model.apiresult.*
import ng.gov.imostate.model.request.*
import ng.gov.imostate.model.response.*
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("agent/vehicle/onboard")
    suspend fun onboardVehicle(
        @Header("Authorization") token: String,
        @Body onboardVehicleRequest: OnboardVehicleRequest
    ): ApiResponse<OnboardVehicleResult>

    @POST("users/login")
    suspend fun login(
        @Header("Authorization") token: String = "Bearer 1|WDFNGg5cqAcnR4IjDljaAtwPXjaVnhiaY72JUdv4",
        @Body loginRequest: LoginRequest
    ): ApiResponse<LoginResult>

    @Headers("Content-Type: application/json")
    @POST("agent/vehicle/vend/{id}")
    suspend fun createSyncTransactions(
        @Header("Authorization") token: String,
        @Path("id") vehicleId: String,
        @Body data: CreateSyncTransactionsRequest
    ): ApiResponse<SyncTransactionsResult>

    @GET("agent/vehicle/{id}")
    suspend fun getVehicle(
        @Header("Authorization") token: String,
        @Body getVehicleRequest: GetVehicleRequest,
    ): ApiResponse<Any>

    @GET("agent/vehicle/all")
    suspend fun getAllVehicles(
        @Header("Authorization") token: String
    ): ApiResponse<VehiclesResult>

    @GET("agent/vehicle/metrics")
    suspend fun getDashboardMetrics(
        @Header("Authorization") token: String
    ): ApiResponse<MetricsResult>

    @GET("agent/vehicle/transaction")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): ApiResponse<TransactionsResult>

    @GET("agent/vehicle/transaction/{id}")
    suspend fun getRecentTransaction(
        @Header("Authorization") token: String,
        @Path("id") vehicleId: String,
    ): ApiResponse<TransactionResult>

}