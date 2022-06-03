package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class SuccessFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(
    userPreferencesRepository
){

}