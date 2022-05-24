package ng.gov.imostate.repository

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ng.gov.imostate.database.dao.RateLocalDao
import ng.gov.imostate.database.dao.RouteLocalDao
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.datasource.RemoteDatasource
import ng.gov.imostate.model.request.FundWalletAccountDetailsRequest
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.response.ApiResponse
import ng.gov.imostate.model.result.ApiResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject

class AgentRepository @Inject constructor(
    private val moshi: Moshi,
    @ApplicationContext private val context: Context,
    private val dataSource: RemoteDatasource,
    private val ratesLocalDao: RateLocalDao,
    private val routeLocalDao: RouteLocalDao,
    private val networkConnectivityUtil: NetworkConnectivityUtil
    ): BaseRepository() {

    suspend fun loginAgent(loginRequest: LoginRequest) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.loginAgent(loginRequest)
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

    suspend fun getCurrentUser(token: String) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getCurrentUser(token)
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

    suspend fun getDashBoardMetrics(token: String,) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getDashBoardMetrics(token)
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

    suspend fun getRates(token: String) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getRates(token)
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

    suspend fun getFundWalletAccountDetails(
        token: String,
        fundWalletAccountDetailsRequest: FundWalletAccountDetailsRequest) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getFundWalletAccountDetails(token, fundWalletAccountDetailsRequest)
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

    suspend fun getAllRatesInDatabase(): List<RateEntity> {
        return ratesLocalDao.getAllRates()
    }

    suspend fun getAllRoutesInDatabase(): List<RouteEntity> {
        return routeLocalDao.getAllRoutes()
    }

    suspend fun getRateInDatabase(category: String): RateEntity? {
        return ratesLocalDao.getRate(category)
    }

    suspend fun insertRatesToDatabase(rates: List<RateEntity>) {
        ratesLocalDao.insertAllRates(rates)
    }

    suspend fun insertRoutesToDatabase(routes: List<RouteEntity>) {
        routeLocalDao.insertAllRoutes(routes)
    }

    suspend fun getAllCategoriesInDatabase(): List<String> {
        return ratesLocalDao.getAllCategories()
    }
}