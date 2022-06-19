package ng.gov.imostate.ui

//import timber.log.Timber
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.BuildConfig
import ng.gov.imostate.R
import ng.gov.imostate.databinding.ActivityMainBinding
import ng.gov.imostate.model.domain.Data
import ng.gov.imostate.service.TransAppFirebaseMessagingService
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.viewmodel.*
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var intentFiltersArray = arrayOf<IntentFilter>()
    private var techListsArray = arrayOf<Array<String>>()
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var navController: NavController
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    @Inject
    lateinit var moshi: Moshi
    private lateinit var activityViewModel: MainActivityViewModel
    private val sharedNfcViewModel: SharedNfcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityViewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(MainActivityViewModel::class.java)

        Timber.d("Welcome")

        navController = findNavController(R.id.activity_main_nav_host_fragment)

        try {
            //init nfc adapter
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        } catch (e: NullPointerException) {
            AppUtils.showToast(this, "This app only works on NFC enabled devices", MotionToastStyle.ERROR)
            finish()
        }

        //init pending intent
        pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }, FLAG_MUTABLE)
        //init nfc tech discovered filter
        val techDiscoveredIntentFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        //init nfc ndef discovered filter
//        val ndefDiscoveredIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
//            addDataType("*/*")
//        }
        //init array of all intent filters
        intentFiltersArray = arrayOf(techDiscoveredIntentFilter)
        //init tech list of all nfc technologies to be handled
        techListsArray = arrayOf(
            arrayOf<String>(
                Ndef::class.java.name
            )
        )

//FIREBASE CLOUD MESSAGING
        //On initial startup of your app, the FCM SDK generates a registration token for
        //the client app instance.
        //you'll need to access this token by extending FirebaseMessagingService and
        //overriding onNewToken.

        //if app has Google play services installed, go ahead and retrieve token
        if (checkGooglePlayServices()) {
            Timber.d("Device has google play services")
            //retrieve token
            getFCMRegistrationToken()

        } else {
            //You won't be able to send notifications to this device
            Timber.d("Device doesn't have google play services")

        }
//FIREBASE CLOUD MESSAGING

        Timber.d("On Create intent: $intent")
        Timber.d("On Create action: ${intent.action}")
        Timber.d("On Create extras: ${intent.extras}")

//PROCESS DATA PAYLOAD OF FCM NOTIFICATION RECEIVED IN BACKGROUND AND OPENED(CLICKED) WHILE
//APP IS STILL IN BACKGROUND
        val title = intent.extras?.getString(DATA_PAYLOAD_TITLE_KEY)
        val body = intent.extras?.getString(DATA_PAYLOAD_BODY_KEY)
        Timber.d("payload title: $title payload body: $body")
        if (title != null && body != null) {
            goToUpdatesScreen(
                title,
                body,
                AppUtils.getCurrentFullDateTime()
            )
        }


    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Timber.d("Error")
            //Ask user to update google play services and manage the error.
            false
        } else {
            Timber.d("Google play services updated")
            true
        }

    }

    private fun getFCMRegistrationToken() {
        //Because the token could be rotated after initial startup, you are strongly recommended
        //to retrieve the latest updated registration token.

        //To retrieve the registration token for the client app instance
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.d("Fetching FCM registration token failed: ${task.exception}")
                return@OnCompleteListener
            }

            //Get new FCM registration token if task was successful
            val token = task.result

            //The device token is a unique identifier that contains two things:
            //- Which device will receive the notification.
            //- The app within that device that will receive the notification.

            // Retrieve token as a String, Log and toast it
            val msg = getString(R.string.msg_token_fmt, token)
            Timber.d(msg)
            //AppUtils.showToast(this, msg, MotionToastStyle.INFO)

            //When the device token is retrieved, Firebase can now connect with the device

        })

    }


    public override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Timber.d("New Intent: $intent")
        resolveIntent(intent)
    }

    private fun resolveIntent(intent: Intent) {
        lifecycleScope.launchWhenResumed {
            if (activityViewModel.getInitialUserPreferences().loggedIn == true) {
                when (intent.action) {
                    NfcAdapter.ACTION_TECH_DISCOVERED -> {
                        if (navController.currentDestination?.id == R.id.findVehicleDialogFragment) {
                            navController.popBackStack()
                        }

                        Timber.d("NFC TECH DISCOVERED")
                        val tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                        if (tagFromIntent != null) {
                            Timber.d("Tag tech list: ${tagFromIntent.techList}")
                            if (sharedNfcViewModel.getNfcMode() == NfcMode.READ.name) {
                                readTag(tagFromIntent)
                            } else {
                                //writeTestDataToTag(tagFromIntent)
                                writeDataToTag(tagFromIntent, sharedNfcViewModel.getData())
                                //reset to read mode. This is the default mode and must be rest back
                                //after every write operation
                                sharedNfcViewModel.setNfcMode(NfcMode.READ)
                                //reset data to null after write operation
                                sharedNfcViewModel.setData(null)
                            }

                        } else {
                            Timber.d("Tag is null")
                            AppUtils.showToast(this@MainActivity, "Tag not found!", MotionToastStyle.ERROR)
                        }
                    }
                    TransAppFirebaseMessagingService.FIREBASE_MESSAGING_EVENT -> {
                        Timber.d("Title Payload: ${intent.extras?.getString(DATA_PAYLOAD_TITLE_KEY)}")
                        Timber.d("Body Payload: ${intent.extras?.getString(DATA_PAYLOAD_BODY_KEY)}")
                        Timber.d("Time Payload: ${intent.extras?.getString(DATA_PAYLOAD_TIME_KEY)}")

                        goToUpdatesScreen(
                            intent.extras?.getString(DATA_PAYLOAD_TITLE_KEY),
                            intent.extras?.getString(DATA_PAYLOAD_BODY_KEY),
                            intent.extras?.getString(DATA_PAYLOAD_TIME_KEY)
                        )
                    }
                    else -> {
                        //PROCESS DATA PAYLOAD OF FCM NOTIFICATION RECEIVED IN BACKGROUND AND OPENED
                        //(CLICKED) WHILE APP IS IN FOREGROUND
                        val title = intent.extras?.getString(DATA_PAYLOAD_TITLE_KEY)
                        val body = intent.extras?.getString(DATA_PAYLOAD_BODY_KEY)
                        Timber.d("payload title: $title payload body: $body")
                        if (title != null && body != null) {
                            goToUpdatesScreen(
                                title,
                                body,
                                AppUtils.getCurrentFullDateTime()
                            )
                        }
                    }

                }
            } else {
                AppUtils.showToast(this@MainActivity, "You need to login first", MotionToastStyle.INFO)
            }
        }
    }

    private fun goToUpdatesScreen(title: String?, body: String?, time: String?) {
        val bundle = Bundle().also {
            it.putString(DATA_PAYLOAD_TITLE_KEY, title)
            it.putString(DATA_PAYLOAD_BODY_KEY, body)
            it.putString(DATA_PAYLOAD_TIME_KEY, time)
            it.putBoolean(DATA_PUSH_NOTIFICATION_KEY, true)
        }
        if (navController.currentDestination?.id == R.id.updatesFragment) {
            navController.popBackStack()
        }
        navController.navigate(
            R.id.updatesFragment,
            bundle,
            NavOptions.Builder().setLaunchSingleTop(true).build()
        )
    }

    private fun writeDataToTag(tagFromIntent: Tag, data: Data?) {
        Timber.d("Writing To Tag")
        val tagData = readAndReturnTagData(tagFromIntent)
        //only write to tag if their vpn are the same otherwise if the tag is null which could
        //mean that it is a new tag that hasn't been onboarded yet with any record,
        //then write to it
        Timber.d("tagData: ${tagData} dataToWrite: $data")
        if (tagData != null) {
//            tagData.vpn == data?.vpn
            if (true) {
                val records = arrayListOf<NdefRecord>()
                val jsonRecord = createNdefJsonRecord(data)
                if (jsonRecord != null) {
                    records.add(jsonRecord)
                    //this will embed the package name of the application inside an NDEF record, thereby
                    //preventing other applications from filtering for the same intent and
                    //potentially handling the data in tag
                    records.add(createAndroidApplicationRecord())
                    writeNdefRecordsToTag(records.toTypedArray(), tagFromIntent)
                } else {
                    AppUtils.showToast(this, "Can't create record", MotionToastStyle.ERROR)
                }
            } else {
                AppUtils.showToast(this, "Tag and vehicle are incompatible", MotionToastStyle.ERROR)
                sharedNfcViewModel.setNfcSyncMode(NfcSyncMode.ERROR)
            }
        } else {
            val records = arrayListOf<NdefRecord>()
            val jsonRecord = createNdefJsonRecord(data)
            if (jsonRecord != null) {
                records.add(jsonRecord)
                //this will embed the package name of the application inside an NDEF record, thereby
                //preventing other applications from filtering for the same intent and
                //potentially handling the data in tag
                records.add(createAndroidApplicationRecord())
                writeNdefRecordsToTag(records.toTypedArray(), tagFromIntent)
            } else {
                AppUtils.showToast(this, "Can't create record", MotionToastStyle.ERROR)
            }
        }
    }
    private fun createNdefJsonRecord(data: Data?): NdefRecord? {
        val json: String
        try {
            if (data != null) {
                json = AppUtils.convertToJson(data, this, moshi)
            } else {
                AppUtils.showToast(this, "Data not found", MotionToastStyle.ERROR)
                return null
            }
        } catch (e: Exception) {
            Timber.d(e.localizedMessage)
            AppUtils.showToast(this, "Data Conversion to JSON failed", MotionToastStyle.ERROR)
            return null
        }

        return NdefRecord.createMime(
            "application/json",
            json.toByteArray(Charset.forName("US-ASCII"))
        )
    }

    private fun createAndroidApplicationRecord(): NdefRecord {
        return NdefRecord.createApplicationRecord(BuildConfig.APPLICATION_ID)
    }

    private fun writeNdefRecordsToTag(records: Array<NdefRecord>, tag: Tag) {
        Timber.d("Write NDEF records to tag")
        val ndef = Ndef.get(tag)
        Timber.d("${ndef.maxSize}")
        val message = NdefMessage(records)
        try {
            ndef.connect()
            ndef.writeNdefMessage(message)
            //AppUtils.showToast(this, "Successfully synced tag", MotionToastStyle.SUCCESS)
            ndef.close()
            sharedNfcViewModel.setNfcSyncMode(NfcSyncMode.SYNCED)
        } catch (e: Exception) {
            AppUtils.showToast(this, e.message, MotionToastStyle.ERROR)
            Timber.d(e.message)
            ndef.close()
        }
    }

    private fun writeEmptyDataToTag(tagFromIntent: Tag) {
        Timber.d("Writing empty data to tag")
        val ndef = Ndef.get(tagFromIntent)
        Timber.d("${ndef.maxSize}")
        val message = createFirstNdefEmptyMessage()
        try {
            ndef.connect()
            ndef.writeNdefMessage(message)
        } catch (e: Exception) {
            AppUtils.showToast(this, e.message, MotionToastStyle.ERROR)
            Timber.d(e.message)
        } finally {
            ndef.close()
        }
    }


    private fun writeTestDataToTag(tagFromIntent: Tag) {
        Timber.d("Writing To Tag")
        val records = arrayListOf<NdefRecord>()
        createNdefTestJsonRecord()?.let { records.add(it) }
        //this will embed the package name of the application inside an NDEF record, thereby
        //preventing other applications from filtering for the same intent and
        //potentially handling the data in tag
        records.add(createAndroidApplicationRecord())
        writeNdefRecordsToTag(records.toTypedArray(), tagFromIntent)
    }

    private fun formatTagAsNdef(ndefFormatable: NdefFormatable?) {
        try {
            ndefFormatable?.connect()
            ndefFormatable?.format(createFirstNdefEmptyMessage())
        } catch (e: Exception) {
            AppUtils.showToast(this, e.message, MotionToastStyle.ERROR)
            Timber.d(e.message)
        } finally {
            ndefFormatable?.close()
        }
    }

    private fun createNdefTextRecord(payload: String, locale: Locale, encodeInUtf8: Boolean): NdefRecord {
        val langBytes = locale.language.toByteArray(Charset.forName("US-ASCII"))
        val utfEncoding = if (encodeInUtf8) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val textBytes = payload.toByteArray(utfEncoding)
        val utfBit: Int = if (encodeInUtf8) 0 else 1 shl 7
        val status = (utfBit + langBytes.size).toChar()
        val data = ByteArray(1 + langBytes.size + textBytes.size)
        data[0] = status.toByte()
        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), data)
    }

    private fun createFirstNdefEmptyMessage(): NdefMessage {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val data = AppUtils.loadJsonFromAsset("empty_data.json", this, moshi)
        val json = data?.let { AppUtils.convertToJson(it, this, moshi) }

        val records = arrayListOf<NdefRecord>()
        val record = NdefRecord.createMime(
            "application/json",
            json?.toByteArray(Charset.forName("US-ASCII"))
        )
        records.add(record)
        //this will embed the package name of the application inside an NDEF record, thereby
        //preventing other applications from filtering for the same intent and
        //potentially handling the data in tag
        records.add(createAndroidApplicationRecord())
        return NdefMessage(records.toTypedArray())
    }

    private fun createNdefTestJsonRecord(): NdefRecord? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val data = AppUtils.loadJsonFromAsset("test_data.json",this, moshi)
        val json = data?.let { AppUtils.convertToJson(it, this, moshi) }

        return NdefRecord.createMime(
            "application/json",
            json?.toByteArray(Charset.forName("US-ASCII"))
        )
    }

    private fun readAndReturnTagData(tag: Tag): Data? {
        var tagData: Data? = null
        Timber.d("Reading Tag from Intent")
        //log tag
        Timber.d("$tag")
        //log and toast payload
        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            if (ndef.ndefMessage.records != null) {
                ndef.ndefMessage.records.map {
                    //you only need to work with the first record, which has a MIME type of "application/json"
                    //and not the second record (application record) which has a MIME type of "android.com:pkg"
                    if (it.toMimeType() == "application/json") {
                        val json = String(it.payload, Charset.forName("US-ASCII"))
                        val moshi = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build()

                        val data = AppUtils.convertToData(json, moshi)

                        tagData = if (data != null) {
                            data
                        } else {
                            Timber.d("Tag contains record but data conversion returns null")
                            Data()
                        }
                    }
                    //log tag content of all records
                    Timber.d("PAYLOAD ${String(it.payload, Charset.forName("US-ASCII"))}")
                    Timber.d("ID ${String(it.id, Charset.forName("US-ASCII"))}")
                    Timber.d("TYPE ${String(it.type, Charset.forName("US-ASCII"))}")
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message)
            //I commented this out because of an exception is caught, it invalidates tag and vehicle pairing
//            tagData = Data()
        } finally {
            ndef.close()
        }
        return tagData
    }

    private fun readTag(tag: Tag) {
        Timber.d("Reading Tag from Intent")
        //log tag
        Timber.d("$tag")
        //log and toast payload
        val ndef = Ndef.get(tag)
        try {
            ndef.connect()
            if (ndef.ndefMessage.records != null) {
                ndef.ndefMessage.records.map {
                    //you only need to work with the first record, which has a MIME type of "application/json"
                    //and not the second record (application record) which has a MIME type of "android.com:pkg"
                    if (it.toMimeType() == "application/json") {
                        val json = String(it.payload, Charset.forName("US-ASCII"))
                        val moshi = Moshi.Builder()
                            .add(KotlinJsonAdapterFactory())
                            .build()

                        val data = AppUtils.convertToData(json, moshi)

                        if (data != null) {
                            if (data.vid == "" && data.lpd == "" && data.vc == "") {
                                Timber.d("Record is empty")
                                AppUtils.showToast(
                                    this,
                                    "Record is empty!",
                                    MotionToastStyle.INFO
                                )
                            } else {
                                AppUtils.showToast(
                                    this,
                                    "Vehicle: ${data.vpn}",
                                    MotionToastStyle.INFO
                                )
                                //display tag payload
                                if (navController.currentDestination?.id != R.id.successFragment) {
                                    goToScannedTagResultScreen(data)
                                }
                            }
                        } else {
                            AppUtils.showToast(
                                this,
                                "Payload conversion failed!",
                                MotionToastStyle.INFO
                            )
                        }
                    }
                    //log tag content of all records
                    Timber.d("PAYLOAD ${String(it.payload, Charset.forName("US-ASCII"))}")
                    Timber.d("ID ${String(it.id, Charset.forName("US-ASCII"))}")
                    Timber.d("TYPE ${String(it.type, Charset.forName("US-ASCII"))}")
                }
            } else {
                AppUtils.showToast(this, "No record found", MotionToastStyle.ERROR)
            }
        } catch (e: Exception) {
            AppUtils.showToast(this, e.message, MotionToastStyle.ERROR)
            Timber.d(e.message)
        } finally {
            ndef.close()
        }
    }

    private fun goToScannedTagResultScreen(data: Data) {
        val bundle = Bundle().also {
            it.putString(VEHICLE_ID_NUMBER_KEY, data.vid)
            it.putString(LAST_PAYMENT_DATE_KEY, data.lpd)
            it.putString(VEHICLE_CATEGORY_KEY, data.vc)
            it.putString(VEHICLE_PLATES_NUMBER_KEY, data.vpn)
        }
        if (navController.currentDestination?.id == R.id.nfcReaderResultFragment) {
            navController.popBackStack()
        }
        navController.navigate(
            R.id.nfcReaderResultFragment,
            bundle,
            NavOptions.Builder().setLaunchSingleTop(true).build()
        )
    }

    companion object {
        const val DATA_PUSH_NOTIFICATION_KEY = "PushNotification"
        const val DATA_PAYLOAD_TIME_KEY = "time"
        const val VEHICLE_LICENSE_EXPIRY_DATE_KEY = "VLEDK"
        const val VEHICLE_PLATES_NUMBER_KEY = "VPN"
        const val VEHICLE_ID_NUMBER_KEY = "VID"
        const val LAST_PAYMENT_DATE_KEY = "LPD"
        const val VEHICLE_CATEGORY_KEY = "VC"
        const val DATE_ONBOARDED_KEY = "DOK"
        const val DATA_PAYLOAD_BODY_KEY = "body"
        const val DATA_PAYLOAD_TITLE_KEY = "title"
    }

}