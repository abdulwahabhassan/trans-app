package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.response.User
import ng.gov.imostate.repository.AppConfigRepository
import javax.inject.Inject


@HiltViewModel
class ProfileActivityViewModel @Inject constructor(
    appConfigRepository: AppConfigRepository
) : BaseViewModel(
    appConfigRepository
)