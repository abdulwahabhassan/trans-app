package ng.gov.imostate.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.ui.AppConfigRepository
import ng.gov.imostate.util.NetworkConnectivityUtil
import javax.inject.Inject


@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    appConfigRepository: AppConfigRepository
) : BaseViewModel(appConfigRepository) {

    private val _connectionState = MutableStateFlow(value = ViewModelResult.Success(false))
    val connectionState = _connectionState

    //    observer for internet connectivity status live-data
    private val activeNetworkStateObserver: androidx.lifecycle.Observer<Boolean> =
        androidx.lifecycle.Observer<Boolean> { isConnected ->
            _connectionState.value = ViewModelResult.Success(isConnected)
        }

    init {
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.observeForever(activeNetworkStateObserver)
    }

    override fun onCleared() {
        super.onCleared()
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.removeObserver(activeNetworkStateObserver)
    }

}