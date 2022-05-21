package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ng.gov.imostate.model.apiresult.VehicleResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
import timber.log.Timber

open class BaseViewModel (
    protected val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    private var _userPreferences:
            MutableStateFlow<ViewModelResult<UserPreferencesRepository.UserPreferences>> =
        MutableStateFlow(ViewModelResult.Success(UserPreferencesRepository.UserPreferences()))

    val userPreferences:
            StateFlow<ViewModelResult<UserPreferencesRepository.UserPreferences>> = _userPreferences

    init {
       observeUserPreferences()
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow
                .catch {userPreferences ->
                    _userPreferences.value = ViewModelResult.Error(
                        userPreferences.localizedMessage ?: "Unknown error"
                    )
                }.collect { userPreferences ->
                _userPreferences.value = ViewModelResult.Success(userPreferences)
                }
        }
    }

    suspend fun getInitialUserPreferences(): UserPreferencesRepository.UserPreferences {
        Timber.d("fecthing pref")
        return userPreferencesRepository.fetchInitialPreferences()
    }

    suspend fun updateUserPreference(userPreferences: UserPreferencesRepository.UserPreferences) {
        Timber.d("$userPreferences")
        userPreferencesRepository.updateUserPreferences(userPreferences)
    }

    suspend fun updateAgentLogInStatus(loggedIn: Boolean) {
        userPreferencesRepository.updateLoginStatus(loggedIn)
    }

    suspend fun updateLastSyncTime(time: String) {
        userPreferencesRepository.updateLastSyncTime(time)
    }

}