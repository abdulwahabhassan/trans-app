package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import ng.gov.imostate.model.UserPreferences
import ng.gov.imostate.repository.AppConfigRepository
import timber.log.Timber

open class BaseViewModel (
    protected val appConfigRepository: AppConfigRepository
): ViewModel() {

    suspend fun getUserPreferences(): AppConfigRepository.AppConfigPreferences {
        Timber.d("fecthing pref")
        return appConfigRepository.fetchInitialPreferences()
    }

    suspend fun updateUserPreference(userPreferences: UserPreferences) {
        Timber.d("$userPreferences")
        appConfigRepository.updateAppConfig(userPreferences)
    }

    suspend fun updateAgentLogInStatus(loggedIn: Boolean) {
        appConfigRepository.updateLoginStatus(loggedIn)
    }
}