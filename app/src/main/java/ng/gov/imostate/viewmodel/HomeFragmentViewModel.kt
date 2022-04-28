package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ng.gov.imostate.model.response.MetricsResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.AppConfigRepository
import ng.gov.imostate.util.NetworkConnectivityUtil
import javax.inject.Inject


@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    appConfigRepository: AppConfigRepository,
    private val agentRepository: AgentRepository
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

    suspend fun getDashBoardMetrics(token: String,): ViewModelResult<MetricsResult?> {
        val response = agentRepository.getDashBoardMetrics(token)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        NetworkConnectivityUtil.instance?.networkConnectivityStatus
            ?.removeObserver(activeNetworkStateObserver)
    }

}