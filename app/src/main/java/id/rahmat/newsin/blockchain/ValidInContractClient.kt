package id.rahmat.newsin.blockchain

import org.json.JSONObject
import org.json.JSONArray
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL

class ValidInContractClient {
    fun verify(rpcUrl: String, contractAddress: String, docHashHex: String): VerificationResult {
        require(rpcUrl.startsWith("http")) { "Layanan registry belum valid." }
        require(contractAddress.matches(Regex("^0x[0-9a-fA-F]{40}$"))) {
            "Contract address belum valid."
        }
        require(docHashHex.matches(Regex("^0x[0-9a-fA-F]{64}$"))) {
            "Hash dokumen belum valid."
        }

        val data = DOCUMENT_STATUS_SELECTOR + docHashHex.removePrefix("0x")
        val call = JSONObject()
            .put("to", contractAddress)
            .put("data", data)
        val params = JSONArray()
            .put(call)
            .put("latest")
        val payload = JSONObject()
            .put("jsonrpc", "2.0")
            .put("id", 1)
            .put("method", "eth_call")
            .put("params", params)

        val response = postJson(rpcUrl, payload.toString())
        val json = JSONObject(response)
        if (json.has("error")) {
            throw IllegalStateException(json.getJSONObject("error").optString("message", "RPC error"))
        }

        val result = json.optString("result")
        return decodeDocumentStatus(result)
    }

    private fun postJson(rpcUrl: String, body: String): String {
        val connection = (URL(rpcUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 20000
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
            writer.write(body)
        }

        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }

        val text = stream.bufferedReader().use { it.readText() }
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("Layanan registry gagal merespons (${connection.responseCode}): $text")
        }
        return text
    }

    private fun decodeDocumentStatus(resultHex: String): VerificationResult {
        if (!resultHex.startsWith("0x") || resultHex.length < 2 + WORD_HEX_LENGTH * 6) {
            throw IllegalStateException("Response contract kosong atau ABI tidak cocok.")
        }

        val words = resultHex.removePrefix("0x").chunked(WORD_HEX_LENGTH)
        val exists = words[0].takeLast(1) == "1"
        val active = words[1].takeLast(1) == "1"
        val issuer = "0x${words[2].takeLast(40)}"
        val issuedAt = BigInteger(words[3], 16).toLong()
        val metadataHash = "0x${words[4]}"
        val subjectHash = "0x${words[5]}"

        return VerificationResult(
            exists = exists,
            active = active,
            issuer = issuer,
            issuedAtEpochSeconds = issuedAt,
            metadataHash = metadataHash,
            subjectHash = subjectHash
        )
    }

    companion object {
        private const val WORD_HEX_LENGTH = 64

        // bytes4(keccak256("documentStatus(bytes32)"))
        private const val DOCUMENT_STATUS_SELECTOR = "0xe530656c"
    }
}
