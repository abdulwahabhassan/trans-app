package ng.gov.imostate

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import ng.gov.imostate.util.NetworkCallbackManager
import timber.log.Timber

@HiltAndroidApp
class TransApp : Application() {

    var networkCallbackManager: NetworkCallbackManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        initTimberLog()
        networkCallbackManager = NetworkCallbackManager(applicationContext)
        networkCallbackManager?.checkNetworkState()
        networkCallbackManager?.registerNetworkCallbackEvents()
    }

    private fun initTimberLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}