package id.rahmat.newsin.blockchain

data class VerificationResult(
    val exists: Boolean,
    val active: Boolean,
    val issuer: String,
    val issuedAtEpochSeconds: Long,
    val metadataHash: String,
    val subjectHash: String
) {
    fun summary(): String {
        if (!exists) {
            return "Status: tidak terdaftar di ValidIn Registry.\nDokumen ini belum pernah diterbitkan oleh issuer kampus."
        }

        val status = if (active) "ASLI dan aktif" else "Terdaftar tetapi sudah dicabut"
        return listOf(
            "Status: $status",
            "Issuer: $issuer",
            "Issued at: $issuedAtEpochSeconds",
            "Metadata hash: $metadataHash",
            "Subject hash: $subjectHash"
        ).joinToString("\n")
    }
}
