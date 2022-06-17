package ng.gov.imostate.repository

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import ng.gov.imostate.database.dao.TransactionLocalDao
import ng.gov.imostate.database.entity.HolidayEntity
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.datasource.RemoteDatasource
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.response.ApiResponse
import ng.gov.imostate.model.result.ApiResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import timber.log.Timber
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val moshi: Moshi,
    private val transactionLocalDao: TransactionLocalDao,
    @ApplicationContext private val context: Context,
    private val dataSource: RemoteDatasource,
    private val networkConnectivityUtil: NetworkConnectivityUtil
): BaseRepository() {

    suspend fun createSyncTransactions(token: String, data: CreateSyncTransactionsRequest) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.createSyncTransactions(token, data)
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

    suspend fun getTransactions(token: String,) = withContext(dispatcher) {
        when (val apiResult = coroutineHandler(context, dispatcher, networkConnectivityUtil) {
            dataSource.getTransactions(token)
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

    suspend fun getAllTransactionsInDatabase(): List<TransactionEntity> {
        return transactionLocalDao.getAllTransactions()
    }

    suspend fun deleteAllTransactionsInDatabase(transactions: List<TransactionEntity>) {
        transactionLocalDao.deleteAllTransactions(transactions)
    }

    suspend fun insertTransactionToDatabase(transaction: TransactionEntity) {
        transactionLocalDao.insertTransaction(transaction)
    }

    suspend fun getTransactionInDatabase(vehicleId: String): TransactionEntity? {
        return transactionLocalDao.getTransaction(vehicleId)
    }

    suspend fun insertAllTaxFreeDaysToDatabase(holidays: List<HolidayEntity>) {
        transactionLocalDao.insertAllTaxFreeDays(holidays)
    }

    suspend fun getAllTaxFreeDaysInDatabase(): List<HolidayEntity> {
        return transactionLocalDao.getAllTaxFreeDays()
    }

    suspend fun deleteAllTaxFreeDaysInDatabase(taxFreeDays: List<HolidayEntity>) {
        transactionLocalDao.deleteAllTaxFreeDays(taxFreeDays)
    }

}