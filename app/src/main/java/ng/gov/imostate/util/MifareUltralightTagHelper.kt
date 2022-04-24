package ng.gov.imostate.util

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import java.nio.charset.Charset

class MifareUltralightTagHelper {
    fun writeTag(tag: Tag, tagText: String) {
        MifareUltralight.get(tag)?.use { ultralight ->
            ultralight.connect()
            Charset.forName("US-ASCII").also { usAscii ->
                //can only write four text per page
                ultralight.writePage(4, tagText.toByteArray(usAscii))
            }
        }
    }

    fun readTag(tag: Tag): String? {
        return MifareUltralight.get(tag)?.use { mifare ->
            mifare.connect()
            val payload = mifare.readPages(4)
            String(payload, Charset.forName("US-ASCII"))
        }
    }
}