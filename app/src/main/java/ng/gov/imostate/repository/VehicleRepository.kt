package ng.gov.imostate.repository

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ng.gov.imostate.database.dao.DriverLocalDao
import ng.gov.imostate.database.dao.VehicleLocalDao
import ng.gov.imostate.database.entity.VehicleCurrentEntity
import ng.gov.imostate.database.entity.VehiclePreviousEntity
import ng.gov.imostate.database.entity.VehicleRouteEntity
import ng.gov.imostate.datasource.RemoteDatasource
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.response.ApiResponse
import ng.gov.imostate.model.result.ApiResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val moshi: Moshi,
    private val vehicleLocalDao: VehicleLocalDao,
    private val driverLocalDao: DriverLocalDao,
    @ApplicationContext private val context: Context,
    private val dataSource: RemoteDatasource,
    private val networkConnectivityUtil: NetworkConnectivityUtil
): BaseRepository() {

    suspend fun getAllVehiclesFromCurrentEnumeration(token: String,) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getAllVehiclesFromCurrentEnumeration(token)
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

    suspend fun getAllVehiclesFromPreviousEnumeration(token: String, lastVehicleId: Long) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getAllVehiclesFromPreviousEnumeration(token, lastVehicleId)
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

    suspend fun getVehicle(token: String, vehicleId: String) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getVehicle(token, vehicleId)
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

    suspend fun insertVehiclesFromPreviousEnumerationToDatabase(vehiclePrevious: List<VehiclePreviousEntity>) {
        vehicleLocalDao.insertAllVehiclesFromPreviousEnumeration(vehiclePrevious)
    }

    suspend fun findVehicleDriverRecordFromPreviousEnumerationInDatabase(identifier: String): VehiclePreviousEntity? {
        return vehicleLocalDao.getVehicleDriverRecordFromPreviousEnumeration(identifier)
    }

    suspend fun getLastVehicleIdFromPreviousEnumerationInDatabase(): Long? {
        return vehicleLocalDao.getLastVehicleIdFromPreviousEnumeration()
    }

    suspend fun insertVehiclesFromCurrentEnumerationToDatabase(vehicleCurrent: List<VehicleCurrentEntity>) {
        vehicleLocalDao.insertAllVehiclesFromCurrentEnumeration(vehicleCurrent)
    }

    suspend fun getVehicleFromCurrentEnumerationInDatabase(identifier: String): VehicleCurrentEntity? {
        return vehicleLocalDao.getVehicleFromCurrentEnumeration(identifier)
    }
}