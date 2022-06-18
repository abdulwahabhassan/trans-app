package ng.gov.imostate.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import ng.gov.imostate.BuildConfig
import ng.gov.imostate.R
import ng.gov.imostate.model.domain.Data
import timber.log.Timber
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

object AppUtils {

    fun loadJsonFromAsset(jsonFileName: String, context: Context, moshi: Moshi): Data? {
        val json: Data?
        try {
            val stream = context.assets.open(jsonFileName)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val stringJson = String(buffer, charset("UTF-8"))
            val gson = Gson()
            val testData = moshi.adapter(Data::class.java).fromJson(stringJson)
            json = testData
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return json
    }

    fun convertToJson(data: Data, context: Context, moshi: Moshi): String {
        val json: String
        try {
            json = moshi.adapter(Data::class.java).toJson(data)
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        return json
    }

    fun convertToData(json: String, moshi: Moshi): Data? {
        val data: Data?
        try {
            data = moshi.adapter(Data::class.java).fromJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return data
    }

    fun convertBitmapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, 0)
    }

    fun formatCurrency(value : Any): String{
        val valueToBeFormatted : Number = if (value is String){
            value.toDouble()
        }else{
            value as Number
        }

        val df = DecimalFormat("##,###,##0.00")
        return df.format(valueToBeFormatted)
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        val now = Date()
        return sdf.format(now)
    }

    fun getCurrentFullDate(): String {
        val sdf = SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        val now = Date()
        return sdf.format(now)
    }

    fun getCurrentFullDateTime(): String {
        val sdf = SimpleDateFormat("E, dd MMM yyyy hh:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        val now = Date()
        return sdf.format(now)
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        val now = Date()
        return sdf.format(now)
    }

    fun formatDateTimeToFullDateTime(date: String): String{
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("E, dd MMM yyyy hh:mm", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        return formatter.format(parser.parse(date) ?: "")
    }

    fun formatDateToFullDate(date: String): String{
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("Africa/Lagos")
        return formatter.format(parser.parse(date) ?: "")
    }

    fun getMacAddress(): String {
        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = java.lang.StringBuilder()
                for (b in macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: java.lang.Exception) {
        }
        return "02:00:00:00:00:00"
    }

    private fun isVMMac(mac: ByteArray?): Boolean {
        if (null == mac) return false
        val invalidMacs = arrayOf(
            byteArrayOf(0x00, 0x05, 0x69),
            byteArrayOf(0x00, 0x1C, 0x14),
            byteArrayOf(0x00, 0x0C, 0x29),
            byteArrayOf(0x00, 0x50, 0x56),
            byteArrayOf(0x08, 0x00, 0x27),
            byteArrayOf(0x0A, 0x00, 0x27),
            byteArrayOf(0x00, 0x03, 0xFF.toByte()),
            byteArrayOf(0x00, 0x15, 0x5D)
        )
        for (invalid in invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]
            ) return true
        }
        return false
    }

    fun getAppVersion(context: Context): String? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
        }
        return packageInfo!!.versionName
    }

    fun showToast(context: Activity, message: String?, style: MotionToastStyle, position: Int = Gravity.CENTER_HORIZONTAL) {
        try {
            if (message != null) {
                MotionToast.createColorToast(context,
                    "Trans App",
                    message,
                    style,
                    position,
                    MotionToast.LONG_DURATION + MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(context, R.font.nunito_sans))
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun showView(show: Boolean, view: View) {
        if (show) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    fun showProgressIndicator(show: Boolean, pi: View) {
        if (show) {
            pi.visibility = View.VISIBLE
        } else {
            pi.visibility = View.INVISIBLE
        }
    }

    fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

    fun mapOfLgaStringArray() : Map<String, Int> {
        return mapOf(
            "Abia" to R.array.abia_lgas,
            "Adamawa" to R.array.adamawa_lgas,
            "Akwa Ibom" to R.array.akwa_ibom_lgas,
            "Anambra" to R.array.anambra_lgas,
            "Bauchi" to R.array.bauchi_lgas,
            "Bayelsa" to R.array.bayelsa_lgas,
            "Benue" to R.array.benue_lgas,
            "Borno" to R.array.borno_lgas,
            "Cross River" to R.array.cross_river_lgas,
            "Delta" to R.array.delta_lgas,
            "Ebonyi" to R.array.ebonyi_lgas,
            "Edo" to R.array.edo_lgas,
            "FCT" to R.array.fct_lgas,
            "Ekiti" to R.array.ekiti_lgas,
            "Enugu" to R.array.enugu_lgas,
            "Gombe" to R.array.gombe_lgas,
            "Imo" to R.array.imo_lgas,
            "Jigawa" to R.array.jigawa_lgas,
            "Kaduna" to R.array.kaduna_lgas,
            "Kano" to R.array.kano_lgas,
            "Katsina" to R.array.katsina_lgas,
            "Kebbi" to R.array.kebbi_lgas,
            "Kogi" to R.array.kogi_lgas,
            "Kwara" to R.array.kwara_lgas,
            "Lagos" to R.array.lagos_lgas,
            "Nasarawa" to R.array.nassarawa_lgas,
            "Niger" to R.array.niger_lgas,
            "Ogun" to R.array.ogun_lgas,
            "Ondo" to R.array.ondo_lgas,
            "Osun" to R.array.osun_lgas,
            "Oyo" to R.array.oyo_lgas,
            "Plateau" to R.array.plateau_lgas,
            "Rivers" to R.array.rivers_lgas,
            "Sokoto" to R.array.sokoto_lgas,
            "Taraba" to R.array.taraba_lgas,
            "Yobe" to R.array.yobe_lgas,
            "Zamfara" to R.array.zamfara_lgas
        )
    }

    fun hideKeyboard(context: Context?, view: View?) {
        if (context != null) {
            val imm =
                context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    fun capitalize(word: String): String {
        return word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    private val encryptionMap = mapOf(
        0 to "d",
        1 to "v",
        2 to "k",
        3 to "l",
        4 to "o",
        5 to "q",
        6 to "i",
        7 to "a",
        8 to "z",
        9 to "f",
    )


    private val decryptionMap = mapOf(
        "d" to 0,
        "v" to 1,
        "k" to 2,
        "l" to 3,
        "o" to 4,
        "q" to 5,
        "i" to 6,
        "a" to 7,
        "z" to 8,
        "f" to 9,
    )


    fun encryptLastPaidDate(lpd: String): String {
        Timber.d("lpd data to encrypt: $lpd")
        val encryptedData = lpd.map { char ->
            if (char.toString() != "-") {
                encryptionMap[char.toString().toInt()]
            } else {
                //make "x" represent "-"
                "x"
            }
        }
        Timber.d("encrypted lpd data: ${encryptedData.joinToString("")}")
        return encryptedData.joinToString("")
    }

    fun decryptLastPaidDate(lpd: String): String {
        Timber.d("lpd data to decrypt: $lpd")
        val decryptedData = lpd.map { char ->
            if (char.toString() != "x") {
                decryptionMap[char.toString()].toString()
            } else {
                //make "-" represent "x"
                "-"
            }

        }
        Timber.d("decrypted lpd data: ${decryptedData.joinToString("")}")
        return decryptedData.joinToString("")
    }


}