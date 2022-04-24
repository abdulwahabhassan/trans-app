package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ng.gov.imostate.ui.AppConfigRepository
import javax.inject.Inject

class AppViewModelsFactory @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(HomeFragmentViewModel::class.java) -> {
                return HomeFragmentViewModel(
                    appConfigRepository
                ) as T
            }
        }
        when {
            modelClass.isAssignableFrom(TransactionsFragmentViewModel::class.java) -> {
                return TransactionsFragmentViewModel(
                    appConfigRepository
                ) as T
            }
        }
        when {
            modelClass.isAssignableFrom(ProfileFragmentViewModel::class.java) -> {
                return ProfileFragmentViewModel(
                    appConfigRepository
                ) as T
            }
        }
        when {
            modelClass.isAssignableFrom(AddVehicleFragmentViewModel::class.java) -> {
                return AddVehicleFragmentViewModel(
                    appConfigRepository
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}