package jp.mamori_i.app.util

import android.util.Base64
import java.security.Key
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource


class SecurityUtil {
    companion object {

        private const val ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private val cipherSpec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)

        fun encrypt(publicKey: PublicKey, value: String): String? {
            if (value.isBlank()) return null
            try {
                val cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.ENCRYPT_MODE, publicKey, cipherSpec)
                val bytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
                return Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (e: Exception) {
                throw e
            }
        }

        fun decrypt(privateKey: Key, value: String): String? {
            if (value.isBlank()) return null
            try {
                val cipher: Cipher = Cipher.getInstance(ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, privateKey, cipherSpec)
                val bytes = Base64.decode(value, Base64.DEFAULT)
                val b = cipher.doFinal(bytes)
                return String(b)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}