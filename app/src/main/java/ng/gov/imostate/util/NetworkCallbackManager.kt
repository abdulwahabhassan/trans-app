package ng.gov.imostate.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber


@RequiresApi(Build.VERSION_CODES.M)
class NetworkCallbackManager(val context: Context) : NetworkCallback() {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkConnectivityUtil = NetworkConnectivityUtil.instance
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        .build()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Timber.d( "onAvailable() called: Connected to network")
        networkConnectivityUtil?.setNetworkConnectivityStatus(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Timber.d("onLost() called: with: Lost network connection")
        networkConnectivityUtil?.setNetworkConnectivityStatus(false)
    }

//    registers the Network-Request callback
//    (Note: Register only once to prevent duplicate callbacks)
    fun registerNetworkCallbackEvents() {
        Timber.d("registerNetworkCallbackEvents() called")
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

//    check current Network state
    fun checkNetworkState() {
        try {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkConnectivityUtil?.setNetworkConnectivityStatus(
                networkInfo != null && networkInfo.isConnected
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}