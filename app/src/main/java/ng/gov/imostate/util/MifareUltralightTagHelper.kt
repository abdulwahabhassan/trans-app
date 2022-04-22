package ng.gov.imostate.util

import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import java.nio.charset.Charset

class MifareUltralightTagHelper {
    fun writeTag(tag: Tag, tagText: String) {
        MifareUltralight.get(tag)?.use { ultralight ->
            ultralight.connect()
            Charset.forName("US-ASCII").also { usAscii ->
                ultralight.writePage(4, "love".toByteArray(usAscii))
                ultralight.writePage(5, "live".toByteArray(usAscii))
                ultralight.writePage(6, "play".toByteArray(usAscii))
                ultralight.writePage(7, "pray".toByteArray(usAscii))
                ultralight.writePage(8, "mike".toByteArray(usAscii))
                ultralight.writePage(9, "nana".toByteArray(usAscii))
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