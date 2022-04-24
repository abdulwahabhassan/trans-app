package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.ui.AppConfigRepository
import javax.inject.Inject

open class BaseViewModel (
    protected val appConfigRepository: AppConfigRepository
): ViewModel() {

    suspend fun getAppConfig(): AppConfigRepository.AppConfigPreferences {
        return appConfigRepository.fetchInitialPreferences()
    }
}