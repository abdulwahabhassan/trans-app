package ng.gov.imostate.api

import ng.gov.imostate.model.request.CreateTransactionRequest
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.request.TransactionData
import ng.gov.imostate.model.response.ApiResponse
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("agent/vehicle/onboard")
    suspend fun onboardVehicle(
        @Body onboardVehicleRequest: OnboardVehicleRequest
    ): ApiResponse<Any>

    @Headers("Content-Type: application/json")
    @POST("agent/vehicle/vend/{id}")
    suspend fun createTransactions(
        @Path("id") vehicleId: Int,
        @Body data: CreateTransactionRequest
    ): ApiResponse<Any>

    @GET("agent/vehicle/{id}")
    suspend fun getVehicle(
        @Path("id") vehicleId: Int,
        @Body getVehicleRequest: GetVehicleRequest
    ): ApiResponse<Any>

    @GET("agent/vehicle/all")
    suspend fun getAllVehicles(): ApiResponse<Any>

    @GET("agent/vehicle/metrics")
    suspend fun getDashboardMetrics(): ApiResponse<Any>

    @GET("agent/vehicle/transaction")
    suspend fun getTransactions(): ApiResponse<Any>

}