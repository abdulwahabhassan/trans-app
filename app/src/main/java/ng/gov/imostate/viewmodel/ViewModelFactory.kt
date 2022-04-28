package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.repository.AppConfigRepository
import javax.inject.Inject

class AppViewModelsFactory @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val agentRepository: AgentRepository,
    private val transactionRepository: TransactionRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(HomeFragmentViewModel::class.java) -> {
                return HomeFragmentViewModel(
                    appConfigRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(TransactionsFragmentViewModel::class.java) -> {
                return TransactionsFragmentViewModel(
                    appConfigRepository,
                    transactionRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileActivityViewModel::class.java) -> {
                return ProfileActivityViewModel(
                    appConfigRepository
                ) as T
            }
            modelClass.isAssignableFrom(AddVehicleFragmentViewModel::class.java) -> {
                return AddVehicleFragmentViewModel(
                    appConfigRepository,
                    vehicleRepository
                ) as T
            }
            modelClass.isAssignableFrom(LoginFragmentViewModel::class.java) -> {
                return LoginFragmentViewModel(
                    appConfigRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileActivityViewModel::class.java) -> {
                return ProfileActivityViewModel(
                    appConfigRepository
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}