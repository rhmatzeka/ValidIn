package id.rahmat.newsin.util

import java.io.InputStream
import java.security.MessageDigest

object Sha256 {
    fun fromStream(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        inputStream.use { stream ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = stream.read(buffer)
                if (read <= 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString(prefix = "0x", separator = "") { "%02x".format(it) }
    }

    fun fromText(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString(prefix = "0x", separator = "") { "%02x".format(it) }
    }
}
