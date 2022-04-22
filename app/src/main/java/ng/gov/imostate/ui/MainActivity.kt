package ng.gov.imostate.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.R
import ng.gov.imostate.databinding.ActivityMainBinding
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.MifareUltralightTagHelper
import timber.log.Timber
//import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.io.UnsupportedEncodingException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var intentFiltersArray = arrayOf<IntentFilter>()
    private var techListsArray = arrayOf<Array<String>>()
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("Welcome")

        binding.bottomNavigationView.background = null

        val bottomNavView: BottomNavigationView = binding.bottomNavigationView

        val navController = findNavController(R.id.activity_main_nav_host_fragment)

        bottomNavView.setupWithNavController(navController)

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                 You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                AppUtils.showToast(this@MainActivity, e.message, MotionToastStyle.ERROR)
            }
        }

        intentFiltersArray = arrayOf(ndef)

        techListsArray = arrayOf(arrayOf<String>(MifareUltralight::class.java.name))

        binding.scanFab.setOnClickListener {
            val destination = navController.findDestination(R.id.scanFragment)
            //prevent stacking multiple instances of this destination on the back stack
            if (navController.currentDestination != destination) {
                navController.navigate(R.id.scanFragment)
            }
        }

//        if (nfcAdapter == null) {
//            //AppUtils.showToast(this, "This device does not support NFC", MotionToastStyle.ERROR)
//            Timber.d("This device does not support NFC")
//        } else {
//            //AppUtils.showToast(this, "This device supports NFC", MotionToastStyle.ERROR)
//            Timber.d("This device supports NFC")
//            readFromIntent(intent)
//            //val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, this::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
////            val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
////            tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
//            //val writeTagFlagsFilter = IntentFilter
//        }
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
        val tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        //do something with tag from Intent
        if (tagFromIntent != null) {
            readTagFromIntent(intent, tagFromIntent)
        }
    }

    private fun readTagFromIntent(intent: Intent, tag: Tag) {
        val action = intent.action
        when {
            NfcAdapter.ACTION_TECH_DISCOVERED == action -> {

//                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
//                    val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
//                    // Process the messages array
//                    AppUtils.showToast(this, "${messages}", MotionToastStyle.INFO)
//                    //Timber.d("${messages}")
//                }
                val mifareUltraLight = MifareUltralight.get(tag)
                // Process the tag object
                //AppUtils.showToast(this, "${tag}", MotionToastStyle.INFO)
                Timber.d("ACTION_TECH_DISCOVERED")
                Timber.d("$tag")

                MifareUltralightTagHelper().writeTag(tag, "")

                val tagContent = MifareUltralightTagHelper().readTag(tag)
                AppUtils.showToast(this, "$tagContent", MotionToastStyle.INFO)
                Timber.d("TAG CONTENT: ${tagContent}")

            }
//            NfcAdapter.ACTION_NDEF_DISCOVERED == action -> {
//
//                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
//                    val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
//                    // Process the messages array
//                    AppUtils.showToast(this, "${messages}", MotionToastStyle.INFO)
//                    //Timber.d("${messages}")
//                }
//
//                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//                // Process the tag object
//                AppUtils.showToast(this, "${tag}", MotionToastStyle.INFO)
//                Timber.d("ACTION_NDEF_DISCOVERED")
//                Timber.d("${tag}")
//                Timber.d("${tag?.techList}")
//                Timber.d("${tag?.id}")
//            }
        }
    }

    fun buildTagViews(messages: List<NdefMessage>) {
//        if (messages.isNullOrEmpty()) {
//            return
//        }
//        val payload = messages[0].records[0].payload
//        val textEncoding = if ((payload[0] & 128) == 0) ? "UTF-8" else "UTF-16"
//        val languageCodeLength = payload[0] & 0063
//
//        try {
//            String(payload, languageCodeLength + 1, payload.length - languageCodeLength)
//        } catch (e: UnsupportedEncodingException) {
//            Timber.d("UnsupportedEncoding", e.toString())
//        }
    }

    fun write(text: String, tag: Tag) {
//        val records = {createRecords(text)}
//        val messsage = NdefMessage(records)
//        val ndef = Ndef.get(tag)
//        ndef.connect()
//        ndef.writeNdefMessage(messsage)
//        ndef.close()
    }

    fun createRecord (text: String) {
//        val lang = "en"
//        val textByte = text.encodeToByteArray()
//        val langBytes = lang.toByteArray(charset("US-ASCII"))
//        val langLength = langBytes.size
//        val textLength = textByte.size
//        val payload = ByteArray(1 + langLength + textLength)
//
//        payload[0] = langLength.toByte()
//        System.arraycopy(langBytes, 0, payload, 1, langLength)
//        System.arraycopy(textByte, 0, payload, 1 + langLength, textLength)
//        val ndfRecord = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
//        return recordNFC

    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//        Timber.d("NEW INTENT")
//
//        setIntent(intent)
//        if (intent != null) {
//            readFromIntent(intent)
//        }
//
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action ||
//            NfcAdapter.ACTION_TECH_DISCOVERED == intent?.action ||
//            NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action
//        ) {
//
//            Timber.d("TAG ME!")
//
//            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
//                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
//                // Process the messages array
//                AppUtils.showToast(this, "${messages}", MotionToastStyle.INFO)
//                Timber.d("${messages}")
//            }
//
//            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
//            // Process the tag object
//            AppUtils.showToast(this, "${tag}", MotionToastStyle.INFO)
//            Timber.d("${tag}")
//
//        }
//    }
}