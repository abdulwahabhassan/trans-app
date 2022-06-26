package ng.gov.imostate.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ng.gov.imostate.model.apiresult.TransactionsResult
import ng.gov.imostate.model.apiresult.VehicleResult
import ng.gov.imostate.model.domain.Collection
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject


@HiltViewModel
class TransactionsFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    private val vehicleRepository: VehicleRepository
) : BaseViewModel(
    userPreferencesRepository
){

    private val _transactions:
            MutableStateFlow<ViewModelResult<List<Transaction>>> = MutableStateFlow(value = ViewModelResult.Loading("Loading")
    )
    val transactions: StateFlow<ViewModelResult<List<Transaction>>> = _transactions

    private val _collections:
            MutableStateFlow<ViewModelResult<List<Collection>>> = MutableStateFlow(value = ViewModelResult.Success(emptyList())
    )
    val collections: StateFlow<ViewModelResult<List<Collection>>> = _collections

    init {
        viewModelScope.launch {
            getInitialUserPreferences().token?.let {
                getTransactions(it)
            }
        }
    }

    private suspend fun getTransactions(token: String,) {
        val response = transactionRepository.getTransactions(token)
        return  when (response.success) {
            true -> {
                _transactions.value = ViewModelResult.Success(response.result?.transactions ?: emptyList())
            }
            else -> {
                _transactions.value = ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getVehicle(token: String, vehicleId: String): ViewModelResult<VehicleResult?> {
        val response = vehicleRepository.getVehicle(token, vehicleId)
        return  when (response.success) {
            true -> {
                _collections.value = ViewModelResult.Success(response.result?.vehicle?.collection ?: emptyList())
                ViewModelResult.Success(response.result)
            }
            else -> {
                _collections.value = ViewModelResult.Error(response.message ?: "Unknown Error")
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

}