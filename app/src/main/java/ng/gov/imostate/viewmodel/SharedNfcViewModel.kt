package ng.gov.imostate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SharedNfcViewModel @Inject constructor() : ViewModel() {

    private var _nfcMode:
            MutableStateFlow<String> =
        MutableStateFlow(NfcMode.READ.name)

    val nfcMode:
            StateFlow<String> = _nfcMode

    private var _nfcSyncMode:
            MutableStateFlow<String> =
        MutableStateFlow(NfcSyncMode.UNSYNCED.name)

    val nfcSyncMode:
            StateFlow<String> = _nfcSyncMode

    private var data: Data? = null

    fun setNfcMode(mode: NfcMode) {
        _nfcMode.value = mode.name
    }

    fun setNfcSyncMode(syncMode: NfcSyncMode) {
        _nfcSyncMode.value = syncMode.name
    }

    fun getNfcMode(): String {
        return nfcMode.value
    }

    fun setData(tagData: Data?) {
        data = tagData
    }

    fun getData(): Data? {
        return data
    }

}

enum class NfcMode {
    READ,
    WRITE
}

enum class NfcSyncMode {
    SYNCED,
    UNSYNCED
}