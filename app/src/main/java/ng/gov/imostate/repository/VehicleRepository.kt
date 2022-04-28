package ng.gov.imostate.repository

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ng.gov.imostate.datasource.RemoteDatasource
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.response.ApiResponse
import ng.gov.imostate.model.response.LoginResult
import ng.gov.imostate.model.response.OnboardVehicleResult
import ng.gov.imostate.model.response.VehiclesResult
import ng.gov.imostate.model.result.ApiResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val moshi: Moshi,
    @ApplicationContext private val context: Context,
    private val dataSource: RemoteDatasource,
    private val networkConnectivityUtil: NetworkConnectivityUtil
): BaseRepository() {

    suspend fun getAllVehicles(token: String,) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getAllVehicles(token)
        }) {
            is ApiResult.Success -> {
                Timber.d("$apiResult")
                apiResult.response
            }
            is ApiResult.Error -> {
                Timber.d("$apiResult")
                ApiResponse(message = apiResult.message)
            }
        }
    }

    suspend fun onboardVehicle(token: String, onboardVehicleRequest: OnboardVehicleRequest) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.onboardVehicle(token, onboardVehicleRequest)
        }) {
            is ApiResult.Success -> {
                Timber.d("$apiResult")
                apiResult.response
            }
            is ApiResult.Error -> {
                Timber.d("$apiResult")
                ApiResponse(message = apiResult.message)
            }
        }
    }
}