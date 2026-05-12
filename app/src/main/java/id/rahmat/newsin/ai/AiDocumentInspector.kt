package id.rahmat.newsin.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

class AiDocumentInspector(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun inspect(
        uri: Uri,
        mimeType: String?,
        onResult: (AiReport) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        when {
            mimeType?.startsWith("image/") == true -> inspectImage(uri, onResult, onError)
            mimeType == "application/pdf" -> inspectPdf(uri, onResult, onError)
            mimeType?.startsWith("text/") == true -> inspectText(uri, onResult, onError)
            else -> onResult(analyze("", listOf("Jenis file belum didukung OCR otomatis.")))
        }
    }

    private fun inspectImage(uri: Uri, onResult: (AiReport) -> Unit, onError: (Throwable) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { onResult(analyze(it.text, emptyList())) }
                .addOnFailureListener(onError)
        } catch (error: Throwable) {
            onError(error)
        }
    }

    private fun inspectPdf(uri: Uri, onResult: (AiReport) -> Unit, onError: (Throwable) -> Unit) {
        Thread {
            var descriptor: ParcelFileDescriptor? = null
            var renderer: PdfRenderer? = null
            var page: PdfRenderer.Page? = null
            try {
                descriptor = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: throw IllegalStateException("Tidak bisa membuka PDF")
                renderer = PdfRenderer(descriptor)
                if (renderer.pageCount == 0) throw IllegalStateException("PDF kosong")
                val pageCount = renderer.pageCount

                page = renderer.openPage(0)
                val scale = 2
                val bitmap = Bitmap.createBitmap(
                    page.width * scale,
                    page.height * scale,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val image = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(image)
                    .addOnSuccessListener {
                        val flags = if (pageCount > 1) {
                            listOf("OCR baru membaca halaman pertama dari $pageCount halaman.")
                        } else {
                            emptyList()
                        }
                        onResult(analyze(it.text, flags))
                    }
                    .addOnFailureListener(onError)
            } catch (error: Throwable) {
                onError(error)
            } finally {
                page?.close()
                renderer?.close()
                descriptor?.close()
            }
        }.start()
    }

    private fun inspectText(uri: Uri, onResult: (AiReport) -> Unit, onError: (Throwable) -> Unit) {
        Thread {
            try {
                val text = context.contentResolver.openInputStream(uri).use { input ->
                    requireNotNull(input) { "Tidak bisa membuka file teks" }
                    BufferedReader(InputStreamReader(input)).readText().take(12000)
                }
                onResult(analyze(text, emptyList()))
            } catch (error: Throwable) {
                onError(error)
            }
        }.start()
    }

    private fun analyze(rawText: String, initialFlags: List<String>): AiReport {
        val text = rawText.replace(Regex("\\s+"), " ").trim()
        val lower = text.lowercase(Locale.ROOT)
        val flags = initialFlags.toMutableList()

        val docType = when {
            "sertifikat" in lower -> "Sertifikat"
            "surat tugas" in lower -> "Surat Tugas"
            "aktif kuliah" in lower -> "Surat Aktif Kuliah"
            "magang" in lower -> "Dokumen Magang"
            "logbook" in lower -> "Logbook"
            "transkrip" in lower -> "Transkrip"
            text.isBlank() -> "Belum terdeteksi"
            else -> "Dokumen Kampus"
        }

        val name = firstMatch(text, "(?:Nama|Name)\\s*[:\\-]?\\s*([A-Za-z .']{3,80})")
        val subjectId = firstMatch(text, "(?:NIM|NPM|No\\.? Induk|ID)\\s*[:\\-]?\\s*([A-Za-z0-9.\\-/]{4,32})")
        val date = firstMatch(text, "(\\d{1,2}[\\-/ ](?:\\d{1,2}|[A-Za-z]{3,12})[\\-/ ]\\d{4}|\\d{4}-\\d{2}-\\d{2})")
        val issuer = firstMatch(
            text,
            "(?:Universitas|Fakultas|Program Studi|Himpunan|BEM|Panitia)\\s+([A-Za-z0-9 .,&\\-]{3,80})"
        )

        if (text.length < 80) flags += "Teks hasil OCR terlalu sedikit untuk validasi kuat."
        if (name == null) flags += "Nama pemilik dokumen belum terbaca jelas."
        if (subjectId == null) flags += "NIM/NPM/ID belum terbaca jelas."
        if (date == null) flags += "Tanggal dokumen belum terbaca."
        if (issuer == null) flags += "Penerbit dokumen belum terbaca jelas."

        var score = 45
        if (text.length >= 160) score += 12
        if (docType != "Belum terdeteksi") score += 10
        if (name != null) score += 10
        if (subjectId != null) score += 10
        if (date != null) score += 8
        if (issuer != null) score += 8
        score -= flags.size * 4
        score = score.coerceIn(0, 98)

        return AiReport(
            score = score,
            docType = docType,
            subjectName = name?.trim(),
            subjectId = subjectId?.trim(),
            detectedDate = date?.trim(),
            issuerHint = issuer?.trim(),
            flags = flags,
            extractedText = if (text.isBlank()) "-" else text.take(2000)
        )
    }

    private fun firstMatch(text: String, pattern: String): String? {
        return Regex(pattern, RegexOption.IGNORE_CASE).find(text)?.groupValues?.getOrNull(1)
    }
}
