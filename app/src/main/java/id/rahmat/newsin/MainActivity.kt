package id.rahmat.newsin

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.rahmat.newsin.ai.AiDocumentInspector
import id.rahmat.newsin.blockchain.ValidInContractClient
import id.rahmat.newsin.blockchain.VerificationResult
import id.rahmat.newsin.util.Sha256
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var inspector: AiDocumentInspector
    private lateinit var loginContainer: ScrollView
    private lateinit var appContainer: View
    private lateinit var loginIdInput: EditText
    private lateinit var loginPasswordInput: EditText
    private lateinit var loginErrorText: TextView
    private lateinit var loginButton: Button
    private lateinit var mainTitleText: TextView
    private lateinit var mainSubtitleText: TextView
    private lateinit var userBadgeText: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var homePage: ScrollView
    private lateinit var verifyPage: ScrollView
    private lateinit var adminPage: ScrollView
    private lateinit var historyPage: ScrollView
    private lateinit var profilePage: ScrollView
    private lateinit var homeGreetingText: TextView
    private lateinit var statVerifiedText: TextView
    private lateinit var statReviewText: TextView
    private lateinit var homeLastStatusText: TextView
    private lateinit var quickVerifyButton: Button
    private lateinit var pickDocumentButton: Button
    private lateinit var scanDocumentButton: Button
    private lateinit var verifyButton: Button
    private lateinit var fileNameText: TextView
    private lateinit var hashText: TextView
    private lateinit var aiStatusText: TextView
    private lateinit var extractedText: TextView
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyListText: TextView
    private lateinit var profileNameText: TextView
    private lateinit var profileIdText: TextView
    private lateinit var registryStatusText: TextView
    private lateinit var logoutButton: Button
    private lateinit var pickAdminDocumentButton: Button
    private lateinit var scanAdminDocumentButton: Button
    private lateinit var registerAdminButton: Button
    private lateinit var adminFileNameText: TextView
    private lateinit var adminHashText: TextView
    private lateinit var adminDocTypeInput: EditText
    private lateinit var adminSubjectInput: EditText
    private lateinit var adminResultText: TextView
    private lateinit var adminProgressBar: ProgressBar

    private var selectedUri: Uri? = null
    private var selectedHash: String? = null
    private var selectedFileName: String = ""
    private var adminHash: String? = null
    private var adminFileName: String = ""
    private var userName: String = ""
    private var userId: String = ""
    private var validCount = 0
    private var reviewCount = 0
    private val historyEntries = mutableListOf<String>()
    private val idLocale: Locale = Locale.forLanguageTag("id-ID")

    private companion object {
        const val VALID_NIM = "231011402890"
        const val VALID_PASSWORD = "Rahmat123"
        const val VALID_NAME = "Rahmat Zeka"
    }

    private val documentPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            selectedUri = uri
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers only allow temporary access.
            }
            analyzeDocument(uri)
        }
    }

    private val adminDocumentPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers only allow temporary access.
            }
            analyzeAdminDocument(uri)
        }
    }

    private val verifyCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveCameraBitmap(bitmap, "validin-scan")
            selectedUri = uri
            analyzeDocument(uri, "image/jpeg")
        }
    }

    private val adminCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveCameraBitmap(bitmap, "validin-admin-scan")
            analyzeAdminDocument(uri, "image/jpeg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, keyboard.bottom)
            )
            insets
        }

        inspector = AiDocumentInspector(this)
        bindViews()
        bindActions()
        restoreSession()
    }

    private fun bindViews() {
        loginContainer = findViewById(R.id.loginContainer)
        appContainer = findViewById(R.id.appContainer)
        loginIdInput = findViewById(R.id.loginIdInput)
        loginPasswordInput = findViewById(R.id.loginPasswordInput)
        loginErrorText = findViewById(R.id.loginErrorText)
        loginButton = findViewById(R.id.loginButton)
        mainTitleText = findViewById(R.id.mainTitleText)
        mainSubtitleText = findViewById(R.id.mainSubtitleText)
        userBadgeText = findViewById(R.id.userBadgeText)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        homePage = findViewById(R.id.homePage)
        verifyPage = findViewById(R.id.verifyPage)
        adminPage = findViewById(R.id.adminPage)
        historyPage = findViewById(R.id.historyPage)
        profilePage = findViewById(R.id.profilePage)
        homeGreetingText = findViewById(R.id.homeGreetingText)
        statVerifiedText = findViewById(R.id.statVerifiedText)
        statReviewText = findViewById(R.id.statReviewText)
        homeLastStatusText = findViewById(R.id.homeLastStatusText)
        quickVerifyButton = findViewById(R.id.quickVerifyButton)
        pickDocumentButton = findViewById(R.id.pickDocumentButton)
        scanDocumentButton = findViewById(R.id.scanDocumentButton)
        verifyButton = findViewById(R.id.verifyButton)
        fileNameText = findViewById(R.id.fileNameText)
        hashText = findViewById(R.id.hashText)
        aiStatusText = findViewById(R.id.aiStatusText)
        extractedText = findViewById(R.id.extractedText)
        resultText = findViewById(R.id.resultText)
        progressBar = findViewById(R.id.progressBar)
        historyListText = findViewById(R.id.historyListText)
        profileNameText = findViewById(R.id.profileNameText)
        profileIdText = findViewById(R.id.profileIdText)
        registryStatusText = findViewById(R.id.registryStatusText)
        logoutButton = findViewById(R.id.logoutButton)
        pickAdminDocumentButton = findViewById(R.id.pickAdminDocumentButton)
        scanAdminDocumentButton = findViewById(R.id.scanAdminDocumentButton)
        registerAdminButton = findViewById(R.id.registerAdminButton)
        adminFileNameText = findViewById(R.id.adminFileNameText)
        adminHashText = findViewById(R.id.adminHashText)
        adminDocTypeInput = findViewById(R.id.adminDocTypeInput)
        adminSubjectInput = findViewById(R.id.adminSubjectInput)
        adminResultText = findViewById(R.id.adminResultText)
        adminProgressBar = findViewById(R.id.adminProgressBar)
    }

    private fun bindActions() {
        loginButton.setOnClickListener { login() }
        quickVerifyButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
        }
        pickDocumentButton.setOnClickListener {
            documentPicker.launch(arrayOf("application/pdf", "image/*", "text/*"))
        }
        scanDocumentButton.setOnClickListener {
            verifyCamera.launch(null)
        }
        verifyButton.setOnClickListener {
            verifySelectedDocument()
        }
        pickAdminDocumentButton.setOnClickListener {
            adminDocumentPicker.launch(arrayOf("application/pdf", "image/*", "text/*"))
        }
        scanAdminDocumentButton.setOnClickListener {
            adminCamera.launch(null)
        }
        registerAdminButton.setOnClickListener {
            registerAdminDocument()
        }
        logoutButton.setOnClickListener {
            getPreferences(MODE_PRIVATE).edit().clear().apply()
            selectedHash = null
            loginContainer.visibility = View.VISIBLE
            appContainer.visibility = View.GONE
        }
        bottomNavigation.setOnItemSelectedListener { item ->
            showPage(item.itemId)
            true
        }
    }

    private fun restoreSession() {
        val prefs = getPreferences(MODE_PRIVATE)
        userName = prefs.getString("name", "") ?: ""
        userId = prefs.getString("id", "") ?: ""

        if (userName.isNotBlank() && userId.isNotBlank()) {
            showApp()
        } else {
            loginContainer.visibility = View.VISIBLE
            appContainer.visibility = View.GONE
        }
    }

    private fun login() {
        val id = loginIdInput.text.toString().trim()
        val password = loginPasswordInput.text.toString()

        if (id != VALID_NIM || password != VALID_PASSWORD) {
            loginErrorText.text = "NIM atau password tidak sesuai."
            loginErrorText.visibility = View.VISIBLE
            return
        }

        userName = VALID_NAME
        userId = id
        getPreferences(MODE_PRIVATE).edit()
            .putString("name", userName)
            .putString("id", userId)
            .apply()

        loginErrorText.visibility = View.GONE
        showApp()
    }

    private fun showApp() {
        loginContainer.visibility = View.GONE
        appContainer.visibility = View.VISIBLE
        userBadgeText.text = initials(userName)
        profileNameText.text = userName
        profileIdText.text = "NIM/NPM $userId"
        registryStatusText.text = if (registryConfigured()) {
            "Registry kampus aktif dan siap digunakan"
        } else {
            "Registry belum dikonfigurasi"
        }
        updateHome()
        bottomNavigation.selectedItemId = R.id.nav_home
        showPage(R.id.nav_home)
    }

    private fun showPage(itemId: Int) {
        homePage.visibility = if (itemId == R.id.nav_home) View.VISIBLE else View.GONE
        verifyPage.visibility = if (itemId == R.id.nav_verify) View.VISIBLE else View.GONE
        adminPage.visibility = if (itemId == R.id.nav_admin) View.VISIBLE else View.GONE
        historyPage.visibility = if (itemId == R.id.nav_history) View.VISIBLE else View.GONE
        profilePage.visibility = if (itemId == R.id.nav_profile) View.VISIBLE else View.GONE

        when (itemId) {
            R.id.nav_admin -> {
                mainTitleText.text = "Admin"
                mainSubtitleText.text = "Terbitkan dokumen"
            }
            R.id.nav_verify -> {
                mainTitleText.text = "Verifikasi"
                mainSubtitleText.text = "Periksa dokumen kampus"
            }
            R.id.nav_history -> {
                mainTitleText.text = "Riwayat"
                mainSubtitleText.text = "Aktivitas pemeriksaan"
            }
            R.id.nav_profile -> {
                mainTitleText.text = "Profil"
                mainSubtitleText.text = "Akun dan akses registry"
            }
            else -> {
                mainTitleText.text = "Beranda"
                mainSubtitleText.text = "ValidIn Campus Trust"
            }
        }
    }

    private fun analyzeDocument(uri: Uri, forcedMimeType: String? = null) {
        setBusy(true)
        verifyButton.isEnabled = false
        selectedHash = null
        selectedFileName = displayName(uri)
        fileNameText.text = selectedFileName
        hashText.text = "Fingerprint: menghitung..."
        aiStatusText.text = "AI sedang membaca dokumen..."
        extractedText.text = ""
        resultText.text = "Hasil pemeriksaan akan muncul setelah dokumen dicek."

        Thread {
            try {
                val hash = openDocumentStream(uri).use { input ->
                    requireNotNull(input) { "Tidak bisa membuka dokumen." }
                    Sha256.fromStream(input)
                }
                selectedHash = hash
                runOnUiThread {
                    hashText.text = "Fingerprint: ${hash.take(18)}...${hash.takeLast(12)}"
                    verifyButton.isEnabled = true
                }

                val mimeType = forcedMimeType ?: contentResolver.getType(uri)
                inspector.inspect(
                    uri = uri,
                    mimeType = mimeType,
                    onResult = { report ->
                        runOnUiThread {
                            aiStatusText.text = report.summary()
                            extractedText.text = "Teks terbaca:\n${report.extractedText}"
                            setBusy(false)
                        }
                    },
                    onError = { error ->
                        runOnUiThread {
                            aiStatusText.text = "AI gagal membaca dokumen: ${error.message}"
                            setBusy(false)
                        }
                    }
                )
            } catch (error: Throwable) {
                runOnUiThread {
                    hashText.text = "Fingerprint: gagal dihitung"
                    aiStatusText.text = error.message ?: "Dokumen gagal dianalisis."
                    setBusy(false)
                }
            }
        }.start()
    }

    private fun analyzeAdminDocument(uri: Uri, forcedMimeType: String? = null) {
        adminBusy(true)
        adminHash = null
        adminFileName = displayName(uri)
        adminFileNameText.text = adminFileName
        adminHashText.text = "Fingerprint: menghitung..."
        adminResultText.text = "Menyiapkan fingerprint dokumen..."

        Thread {
            try {
                val hash = openDocumentStream(uri).use { input ->
                    requireNotNull(input) { "Tidak bisa membuka dokumen admin." }
                    Sha256.fromStream(input)
                }
                adminHash = hash
                val defaultType = when (forcedMimeType ?: contentResolver.getType(uri)) {
                    "application/pdf" -> "Dokumen PDF"
                    "image/jpeg", "image/png" -> "Scan Dokumen"
                    else -> "Dokumen Kampus"
                }
                runOnUiThread {
                    adminHashText.text = "Fingerprint: ${hash.take(18)}...${hash.takeLast(12)}"
                    if (adminDocTypeInput.text.isBlank()) adminDocTypeInput.setText(defaultType)
                    if (adminSubjectInput.text.isBlank()) adminSubjectInput.setText("$userId $userName")
                    adminResultText.text = "Dokumen siap didaftarkan."
                    registerAdminButton.isEnabled = true
                    adminBusy(false)
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    adminHashText.text = "Fingerprint: gagal dihitung"
                    adminResultText.text = error.message ?: "Dokumen admin gagal diproses."
                    adminBusy(false)
                }
            }
        }.start()
    }

    private fun registerAdminDocument() {
        val hash = adminHash
        val docType = adminDocTypeInput.text.toString().trim().ifBlank { "Dokumen Kampus" }
        val subject = adminSubjectInput.text.toString().trim().ifBlank { "$userId $userName" }
        val apiUrl = BuildConfig.VALIDIN_ADMIN_API_URL.trim().trimEnd('/')

        if (hash == null) {
            adminResultText.text = "Pilih atau scan dokumen dulu."
            return
        }

        if (!apiUrl.startsWith("http")) {
            adminResultText.text = "Admin API belum dikonfigurasi. Jalankan server admin dan isi VALIDIN_ADMIN_API_URL di .env."
            return
        }

        adminBusy(true)
        adminResultText.text = "Mengirim dokumen ke registry kampus..."

        Thread {
            try {
                val metadata = "$docType | $adminFileName | issuer=$userId"
                val payload = JSONObject()
                    .put("docHash", hash)
                    .put("metadataHash", Sha256.fromText(metadata))
                    .put("subjectHash", Sha256.fromText(subject))
                    .put("docType", docType)

                val response = postJson("$apiUrl/register", payload)
                runOnUiThread {
                    adminResultText.text = "Dokumen berhasil didaftarkan.\n${response.optString("txHash", "")}"
                    adminBusy(false)
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    adminResultText.text = "Registrasi gagal: ${error.message}"
                    adminBusy(false)
                }
            }
        }.start()
    }

    private fun verifySelectedDocument() {
        val hash = selectedHash
        if (hash == null) {
            resultText.text = "Pilih dokumen dulu."
            return
        }

        if (!registryConfigured()) {
            resultText.text = "Registry kampus belum dikonfigurasi di aplikasi."
            return
        }

        setBusy(true)
        resultText.text = "Memeriksa keaslian dokumen melalui registry kampus..."

        Thread {
            try {
                val result = ValidInContractClient().verify(
                    BuildConfig.VALIDIN_RPC_URL,
                    BuildConfig.VALIDIN_CONTRACT_ADDRESS,
                    hash
                )
                runOnUiThread {
                    val formatted = formatVerificationResult(result)
                    resultText.text = formatted
                    recordHistory(result, formatted)
                    setBusy(false)
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    resultText.text = "Verifikasi gagal: ${error.message}"
                    reviewCount += 1
                    updateHome()
                    setBusy(false)
                }
            }
        }.start()
    }

    private fun recordHistory(result: VerificationResult, formatted: String) {
        if (result.exists && result.active) validCount += 1 else reviewCount += 1

        val status = if (result.exists && result.active) "Valid" else "Perlu review"
        val entry = "${historyEntries.size + 1}. $selectedFileName\n$status - ${nowText()}\n${formatted.lineSequence().firstOrNull().orEmpty()}"
        historyEntries.add(0, entry)
        historyListText.text = historyEntries.joinToString(separator = "\n\n")
        homeLastStatusText.text = entry
        updateHome()
    }

    private fun formatVerificationResult(result: VerificationResult): String {
        if (!result.exists) {
            return "Status: Tidak ditemukan\nDokumen ini belum tercatat di registry kampus. Minta admin atau panitia menerbitkan ulang dokumen resmi."
        }

        val status = if (result.active) "Valid dan aktif" else "Terdaftar tetapi sudah dicabut"
        return listOf(
            "Status: $status",
            "Issuer kampus: ${shortAddress(result.issuer)}",
            "Dicatat pada: ${formatTimestamp(result.issuedAtEpochSeconds)}",
            "Metadata: ${result.metadataHash.take(14)}...${result.metadataHash.takeLast(10)}",
            "Pemilik: ${result.subjectHash.take(14)}...${result.subjectHash.takeLast(10)}"
        ).joinToString("\n")
    }

    private fun updateHome() {
        val firstName = userName.split(" ").firstOrNull().orEmpty().ifBlank { "Pengguna" }
        homeGreetingText.text = "Halo, $firstName"
        statVerifiedText.text = "$validCount\nValid"
        statReviewText.text = "$reviewCount\nReview"
        if (historyEntries.isEmpty()) {
            homeLastStatusText.text = "Belum ada dokumen yang diverifikasi."
        }
    }

    private fun displayName(uri: Uri): String {
        if (uri.scheme == "file") return File(uri.path ?: "").name.ifBlank { "Scan kamera" }
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && it.moveToFirst()) return it.getString(index)
        }
        return uri.lastPathSegment ?: "Dokumen dipilih"
    }

    private fun openDocumentStream(uri: Uri): InputStream? {
        return if (uri.scheme == "file") {
            File(uri.path ?: return null).inputStream()
        } else {
            contentResolver.openInputStream(uri)
        }
    }

    private fun saveCameraBitmap(bitmap: Bitmap, prefix: String): Uri {
        val file = File(cacheDir, "$prefix-${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }
        return Uri.fromFile(file)
    }

    private fun postJson(url: String, body: JSONObject): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
            writer.write(body.toString())
        }

        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }
        val text = stream.bufferedReader().use { it.readText() }
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException(text.ifBlank { "Admin API error ${connection.responseCode}" })
        }
        return JSONObject(text)
    }

    private fun setBusy(isBusy: Boolean) {
        progressBar.visibility = if (isBusy) View.VISIBLE else View.GONE
        pickDocumentButton.isEnabled = !isBusy
        scanDocumentButton.isEnabled = !isBusy
        verifyButton.isEnabled = !isBusy && selectedHash != null
    }

    private fun adminBusy(isBusy: Boolean) {
        adminProgressBar.visibility = if (isBusy) View.VISIBLE else View.GONE
        pickAdminDocumentButton.isEnabled = !isBusy
        scanAdminDocumentButton.isEnabled = !isBusy
        registerAdminButton.isEnabled = !isBusy && adminHash != null
    }

    private fun registryConfigured(): Boolean {
        return BuildConfig.VALIDIN_RPC_URL.startsWith("http") &&
            BuildConfig.VALIDIN_CONTRACT_ADDRESS.matches(Regex("^0x[0-9a-fA-F]{40}$"))
    }

    private fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }.ifBlank { "VI" }
    }

    private fun shortAddress(address: String): String {
        if (address.length < 12) return address
        return "${address.take(6)}...${address.takeLast(4)}"
    }

    private fun formatTimestamp(epochSeconds: Long): String {
        if (epochSeconds <= 0) return "-"
        return SimpleDateFormat("dd MMM yyyy, HH:mm", idLocale)
            .format(Date(epochSeconds * 1000))
    }

    private fun nowText(): String {
        return SimpleDateFormat("dd MMM yyyy, HH:mm", idLocale).format(Date())
    }
}
