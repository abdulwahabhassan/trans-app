package ng.gov.imostate.viewmodel

import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ng.gov.imostate.Mapper
import ng.gov.imostate.database.entity.*
import ng.gov.imostate.model.apiresult.*
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(userPreferencesRepository) {

    private val _serverTime = MutableStateFlow(value = "")
    val serverTime = _serverTime

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
                val dashBoardMetrics = response.result?.metrics

                //get all current agent's assigned routes in database
                val databaseRoutes = getAllAgentRoutesInDatabase()
                Timber.d("agents database routes to be deleted $databaseRoutes")
                //delete all current agent's assigned routes in database
                deleteAllAgentRoutesInDatabase(databaseRoutes)

                //insert new retrieved agent's routes to database
                Timber.d("agents remote routes to be inserted ${dashBoardMetrics?.routes}")
                dashBoardMetrics?.routes?.let { routes ->
                    Mapper.mapListOfAgentRouteToListOfAgentRouteEntity(routes)
                }?.let { insertAllAgentRoutesToDatabase(it) }

                //retrieve settings values for whether agent can collect money from vehicles
                //outside their route
                val outsideRouteSetting = dashBoardMetrics?.settings?.find { it.setting_key == "allow_agents_collect_outside_route" }
                //save setting value to appConfig
                updateAgentCollectionSettingValue(outsideRouteSetting?.value ?: "No")

                //retrieve settings values for whether instalments from vehicles are allowed
                val instalmentsSetting = dashBoardMetrics?.settings?.find { it.setting_key == "allow_installmental_payment" }
                //save setting value to appConfig
                updateInstalmentsSettingValue(instalmentsSetting?.value ?: "0")

                //get all current agent's assigned routes in database
                val taxFreeDays = getAllTaxFreeDaysInDatabase()
                Timber.d("tax free days in database to be deleted $taxFreeDays")
                //delete all current agent's assigned routes in database
                deleteAllTaxFreeDaysInDatabase(taxFreeDays)

                //retrieve and save new tax free days to database
                Timber.d("remote tax free days to be inserted ${response.meta?.taxFreeDays}")
                response.meta?.taxFreeDays?.let { holidays ->
                    Mapper.mapListOfTaxFreeDayToListOfTaxFreeDayEntity(holidays)
                }?.let { insertAllTaxFreeDaysToDatabase(it) }

                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun updateInstalmentsSettingValue(value: String) {
        Timber.d("instalments: $value")
        userPreferencesRepository.updateInstalmentsSettingValue(value)
    }

    private suspend fun deleteAllTaxFreeDaysInDatabase(taxFreeDays: List<HolidayEntity>) {
        transactionRepository.deleteAllTaxFreeDaysInDatabase(taxFreeDays)
    }

    private suspend fun getAllTaxFreeDaysInDatabase(): List<HolidayEntity> {
        return transactionRepository.getAllTaxFreeDaysInDatabase()
    }

    private suspend fun insertAllTaxFreeDaysToDatabase(holidays: List<HolidayEntity>) {
        transactionRepository.insertAllTaxFreeDaysToDatabase(holidays)
    }

    private suspend fun insertAllAgentRoutesToDatabase(routes: List<AgentRouteEntity>) {
        agentRepository.insertAllAgentRoutesToDatabase(routes)
    }

    suspend fun getAllVehiclesFromCurrentEnumeration(token: String): ViewModelResult<VehiclesResult?> {
        val response = vehicleRepository.getAllVehiclesFromCurrentEnumeration(token)
        return when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getAllVehiclesFromPreviousEnumeration(token: String, lastVehicleId: Long): ViewModelResult<VehiclesResult?> {
        val response = vehicleRepository.getAllVehiclesFromPreviousEnumeration(token, lastVehicleId)
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

    suspend fun getCurrentUser(token: String): ViewModelResult<CurrentUserResult?> {
        val response = agentRepository.getCurrentUser(token)
        return  when (response.success) {
            true -> {
                Timber.d("current user result ${response.result}")

                Timber.d("server-time: ${response.meta?.serverTime}")
                _serverTime.value = response.meta?.serverTime ?: ""

                //put agent current wallet information to data store
                response.result?.user?.profile?.let {
                    userPreferencesRepository.updateCurrentWalletInfo(
                        response.result.user.profile.currentBalance?.toDouble() ?: 0.00,
                        response.result.user.profile.totalAmountVended?.toDouble() ?: 0.00,
                        response.result.user.profile.totalAmountCredited?.toDouble() ?: 0.00,
                        response.result.user.profile.currentPayable?.toDouble() ?: 0.00,
                        response.result.user.profile.paidOut?.toDouble() ?: 0.00
                    )
                }

                //put rates to database
                response.meta?.rates?.let { rates ->
                    Timber.d("rates $rates")
                    Mapper.mapListOfRateToListOfRateEntity(rates)
                }?.let { agentRepository.insertRatesToDatabase(it) }

                //put routes to database
                response.meta?.route?.let { routes ->
                    Timber.d("routes $routes")
                    Mapper.mapListOfRouteToListOfRouteEntity(routes)
                }?.let { agentRepository.insertRoutesToDatabase(it) }

                ViewModelResult.Success(response.result)

            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun insertVehiclesFromPreviousEnumerationToDatabase(vehicles: List<VehiclePreviousEntity>) {
        vehicleRepository.insertVehiclesFromPreviousEnumerationToDatabase(vehicles)
    }

    suspend fun getAllTransactionsInDatabase(): List<TransactionEntity> {
        return transactionRepository.getAllTransactionsInDatabase()
    }

    suspend fun deleteAllTransactionsInDatabase(transactions: List<TransactionEntity>) {
        transactionRepository.deleteAllTransactionsInDatabase(transactions)
    }

    suspend fun getLastVehicleIdFromPreviousEnumerationInDatabase(): Long? {
        return vehicleRepository.getLastVehicleIdFromPreviousEnumerationInDatabase()
    }

    suspend fun insertVehiclesFromCurrentEnumerationToDatabase(vehicles: List<VehicleCurrentEntity>) {
        vehicleRepository.insertVehiclesFromCurrentEnumerationToDatabase(vehicles)
    }

    private suspend fun getAllAgentRoutesInDatabase(): List<AgentRouteEntity> {
        return agentRepository.getAllAgentRoutesInDatabase()
    }

    private suspend fun deleteAllAgentRoutesInDatabase(routes: List<AgentRouteEntity>) {
        agentRepository.deleteAllAgentRoutesInDatabase(routes)
    }

    private suspend fun updateAgentCollectionSettingValue(value: String) {
        userPreferencesRepository.updateAgentCollectionSettingValue(value)
    }

    override fun onCleared() {
        super.onCleared()
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.removeObserver(activeNetworkStateObserver)
    }



}