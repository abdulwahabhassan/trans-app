package ng.gov.imostate.service

import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ng.gov.imostate.ui.MainActivity
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.NotificationBuilder
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle

class TransAppFirebaseMessagingService: FirebaseMessagingService() {

    //Called if the FCM registration token is updated. This may occur if the security of
    //the previous token had been compromised. Note that this is called when the
    //FCM registration token is initially generated so this is where you would retrieve the token.
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token") //we could save to database

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }

    //When receiving notifications, it’s important to distinguish whether the app is running on the
    //foreground or background. Basically, you need to consider the following:
    //-Foreground: The notification and the data are both handled in onMessageReceived()
    //-Background: The System UI handles the notification.
    override fun onMessageReceived(p0: RemoteMessage) {
        Timber.d("on message received called")
        super.onMessageReceived(p0)
        //handle the message received
        handleMessage(p0)
    }


    private fun handleMessage(remoteMessage: RemoteMessage) {
        Timber.d("handle message called")
        Timber.d("remote message data: ${remoteMessage.data}")

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        //use local broadcaster to show dialog containing notification content instead of displaying
        //notification in the notification tray
        NotificationBuilder(this, body ?: "", title ?: "").postNotification()
    }

    companion object {
        const val FIREBASE_MESSAGING_EVENT = "com.google.firebase.MESSAGING_EVENT"
    }

}