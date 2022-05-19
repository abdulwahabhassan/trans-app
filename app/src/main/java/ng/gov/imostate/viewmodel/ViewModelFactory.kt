package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

class AppViewModelsFactory @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository,
    private val transactionRepository: TransactionRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(HomeFragmentViewModel::class.java) -> {
                return HomeFragmentViewModel(
                    userPreferencesRepository,
                    agentRepository,
                    vehicleRepository,
                    transactionRepository
                ) as T
            }
            modelClass.isAssignableFrom(TransactionsFragmentViewModel::class.java) -> {
                return TransactionsFragmentViewModel(
                    userPreferencesRepository,
                    transactionRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileActivityViewModel::class.java) -> {
                return ProfileActivityViewModel(
                    userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(AddVehicleFragmentViewModel::class.java) -> {
                return AddVehicleFragmentViewModel(
                    userPreferencesRepository,
                    vehicleRepository,
                    transactionRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(LoginFragmentViewModel::class.java) -> {
                return LoginFragmentViewModel(
                    userPreferencesRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                return MainActivityViewModel(
                    userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(FindVehicleDialogFragmentViewModel::class.java) -> {
                return FindVehicleDialogFragmentViewModel(
                    userPreferencesRepository,
                    vehicleRepository
                ) as T
            }
            modelClass.isAssignableFrom(NfcReaderResultFragmentViewModel::class.java) -> {
                return NfcReaderResultFragmentViewModel(
                    userPreferencesRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(SuccessFragmentViewModel::class.java) -> {
                return SuccessFragmentViewModel(
                    userPreferencesRepository,
                    vehicleRepository,
                    transactionRepository
                ) as T
            }
            modelClass.isAssignableFrom(OutstandingPaymentFragmentViewModel::class.java) -> {
                return OutstandingPaymentFragmentViewModel(
                    userPreferencesRepository,
                    transactionRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(VehicleDetailsDialogFragmentViewModel::class.java) -> {
                return VehicleDetailsDialogFragmentViewModel(
                    userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(TagVehicleFragmentViewModel::class.java) -> {
                return TagVehicleFragmentViewModel(
                    userPreferencesRepository
                ) as T
            }
            modelClass.isAssignableFrom(DailyRatesDialogFragmentViewModel::class.java) -> {
                return DailyRatesDialogFragmentViewModel(
                    userPreferencesRepository,
                    agentRepository
                ) as T
            }
            modelClass.isAssignableFrom(FundWalletFragmentViewModel::class.java) -> {
                return FundWalletFragmentViewModel(
                    userPreferencesRepository,
                    agentRepository
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}