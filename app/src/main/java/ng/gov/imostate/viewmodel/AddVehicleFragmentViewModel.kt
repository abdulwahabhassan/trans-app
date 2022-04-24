package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.ui.AppConfigRepository
import javax.inject.Inject


@HiltViewModel
class AddVehicleFragmentViewModel @Inject constructor(
    appConfigRepository: AppConfigRepository
) : BaseViewModel(
    appConfigRepository
){


}