package ng.gov.imostate.viewmodel

import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ng.gov.imostate.Mapper
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.apiresult.*
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository,
    private val moshi: Moshi
) : BaseViewModel(userPreferencesRepository) {

    private val _connectionState = MutableStateFlow(value = ViewModelResult.Success(false))
    val connectionState = _connectionState

    //    observer for internet connectivity status live-data
    private val activeNetworkStateObserver: androidx.lifecycle.Observer<Boolean> =
        androidx.lifecycle.Observer<Boolean> { isConnected ->
            _connectionState.value = ViewModelResult.Success(isConnected)
        }

    init {
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.observeForever(activeNetworkStateObserver)
    }

    suspend fun getDashBoardMetrics(token: String,): ViewModelResult<MetricsResult?> {
        val response = agentRepository.getDashBoardMetrics(token)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getAllVehicles(token: String): ViewModelResult<VehiclesResult?> {
        val response = vehicleRepository.getAllVehicles(token)
        return when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun createSyncTransactions(
        token: String,
        createSyncTransactionsRequest: CreateSyncTransactionsRequest
    ): ViewModelResult<SyncTransactionsResult?> {
        val response = transactionRepository.createSyncTransactions(token, createSyncTransactionsRequest)
        return when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getRates(token: String): ViewModelResult<RatesResult?> {
        val response = agentRepository.getRates(token)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getCurrentUser(token: String): ViewModelResult<CurrentUserResult?> {
        val response = agentRepository.getCurrentUser(token)
        return  when (response.success) {
            true -> {
                //put current wallet balance to data store
                response.result?.user?.profile?.currentBalance?.let { currentBalance ->
                    userPreferencesRepository.updateCurrentWalletBalance(2000.00)
                }

                //put rates to database
                response.meta?.rates?.let { rates ->
                    Mapper.mapListOfRateToListOfRateEntity(rates)
                }?.let { agentRepository.insertRatesToDatabase(it) }

                //put routes to database
                response.meta?.route?.let { routes ->
                    Timber.d("$routes")
                    Mapper.mapListOfRouteToListOfRouteEntity(routes)
                }?.let { agentRepository.insertRoutesToDatabase(it) }

                ViewModelResult.Success(response.result)

            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun insertVehiclesToDatabase(vehicles: List<VehicleEntity>) {
        vehicleRepository.insertVehiclesToDatabase(vehicles)
    }

    suspend fun getAllTransactionsInDatabase(): List<TransactionEntity> {
        return transactionRepository.getAllTransactionsInDatabase()
    }

    suspend fun deleteAllTransactionsInDatabase(transactions: List<TransactionEntity>) {
        transactionRepository.deleteAllTransactionsInDatabase(transactions)
    }

    override fun onCleared() {
        super.onCleared()
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.removeObserver(activeNetworkStateObserver)
    }

}