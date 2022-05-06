package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.dao.VehicleDriverRecord
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.apiresult.OnboardVehicleResult
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject
@HiltViewModel
class VehicleDetailsDialogFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel(
    userPreferencesRepository
){


}