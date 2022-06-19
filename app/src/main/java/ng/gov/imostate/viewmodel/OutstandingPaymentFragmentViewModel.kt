package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.Mapper
import ng.gov.imostate.database.entity.HolidayEntity
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.model.apiresult.MetricsResult
import ng.gov.imostate.model.domain.TransactionData
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class OutstandingPaymentFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun insertTransactionToDatabase(transaction: TransactionData) {
        transactionRepository.insertTransactionToDatabase(
            Mapper.mapTransactionToTransactionEntity(transaction)
        )
    }

    suspend fun getAllTransactionsInDatabase(): List<TransactionEntity> {
        return transactionRepository.getAllTransactionsInDatabase()
    }

    suspend fun getTransactionInDatabase(vehicleId: String): TransactionEntity? {
        return transactionRepository.getTransactionInDatabase(vehicleId)
    }

    suspend fun getRateInDatabase(category: String): RateEntity? {
        return agentRepository.getRateInDatabase(category)
    }

    suspend fun getAllHolidaysInDatabase(): List<HolidayEntity> {
        return transactionRepository.getAllTaxFreeDaysInDatabase()
    }
}