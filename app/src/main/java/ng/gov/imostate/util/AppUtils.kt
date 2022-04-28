package ng.gov.imostate.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Base64
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import ng.gov.imostate.BuildConfig
import ng.gov.imostate.R
import ng.gov.imostate.model.Data
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val now = Date()
        return sdf.format(now)
    }

    fun formatDateTime(date: String): String{
        val formatter = SimpleDateFormat("E, dd MMM yyyy hh:mm", Locale.US)
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
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

    fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
}