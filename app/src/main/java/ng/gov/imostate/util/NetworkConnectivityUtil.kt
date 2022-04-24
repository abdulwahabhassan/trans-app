package ng.gov.imostate.util

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber


class NetworkConnectivityUtil {
//    updates the active network status live-data
    fun setNetworkConnectivityStatus(connectivityStatus: Boolean) {
        Timber.d(
            "setNetworkConnectivityStatus() called with: connectivityStatus = [$connectivityStatus]"
        )
        if (Looper.myLooper() == Looper.getMainLooper()) {
            activeNetworkStatusMLD.setValue(connectivityStatus)
        } else {
            activeNetworkStatusMLD.postValue(connectivityStatus)
        }
    }

//    returns the current network status
    val networkConnectivityStatus: LiveData<Boolean>
        get() {
            Timber.d("getNetworkConnectivityStatus() called")
            return activeNetworkStatusMLD
        }

    companion object {
        private var INSTANCE: NetworkConnectivityUtil? = null
        private val activeNetworkStatusMLD = MutableLiveData<Boolean>()

        @get:Synchronized
        val instance: NetworkConnectivityUtil?
            get() {
                if (INSTANCE == null) {
                    Timber.d("getInstance() called: Creating new instance")
                    INSTANCE = NetworkConnectivityUtil()
                }
                return INSTANCE
            }
    }
}