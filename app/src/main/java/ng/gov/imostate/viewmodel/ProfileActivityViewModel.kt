package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject


@HiltViewModel
class ProfileActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel(
    userPreferencesRepository
)