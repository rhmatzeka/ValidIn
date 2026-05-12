package id.rahmat.newsin.ai

data class AiReport(
    val score: Int,
    val docType: String,
    val subjectName: String?,
    val subjectId: String?,
    val detectedDate: String?,
    val issuerHint: String?,
    val flags: List<String>,
    val extractedText: String
) {
    fun summary(): String {
        val status = when {
            score >= 80 -> "Risiko rendah"
            score >= 55 -> "Perlu review admin"
            else -> "Risiko tinggi"
        }

        val fields = listOf(
            "Status AI: $status ($score/100)",
            "Jenis: $docType",
            "Nama: ${subjectName ?: "-"}",
            "NIM/NPM/ID: ${subjectId ?: "-"}",
            "Tanggal: ${detectedDate ?: "-"}",
            "Penerbit: ${issuerHint ?: "-"}"
        )

        val warningText = if (flags.isEmpty()) {
            "Catatan: tidak ada anomali utama terdeteksi."
        } else {
            "Catatan: ${flags.joinToString("; ")}"
        }

        return (fields + warningText).joinToString("\n")
    }
}
