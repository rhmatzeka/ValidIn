package id.rahmat.newsin

import android.graphics.Bitmap
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import id.rahmat.newsin.ai.AiDocumentInspector
import id.rahmat.newsin.ai.AiReport
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
    private lateinit var roleStudentButton: Button
    private lateinit var roleAdminButton: Button
    private lateinit var topLogoText: ImageView
    private lateinit var mainTitleText: TextView
    private lateinit var mainSubtitleText: TextView
    private lateinit var userBadgeText: TextView
    private lateinit var profileSettingsButton: TextView
    private lateinit var profileEditButton: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var homePage: ScrollView
    private lateinit var verifyPage: ScrollView
    private lateinit var adminPage: ScrollView
    private lateinit var historyPage: ScrollView
    private lateinit var profilePage: ScrollView
    private lateinit var settingsPage: ScrollView
    private lateinit var editProfilePage: ScrollView
    private lateinit var homeSearchInput: EditText
    private lateinit var homeSearchActionButton: TextView
    private lateinit var homeSearchFeedbackText: TextView
    private lateinit var homeHeroTitleText: TextView
    private lateinit var homeHeroSubtitleText: TextView
    private lateinit var statVerifiedText: TextView
    private lateinit var statReviewText: TextView
    private lateinit var homeLastStatusText: TextView
    private lateinit var homeQuickInsightText: TextView
    private lateinit var homeRegistryStatusText: TextView
    private lateinit var homeShortcutScanButton: TextView
    private lateinit var homeShortcutPickButton: TextView
    private lateinit var homeShortcutHistoryButton: TextView
    private lateinit var homeTotalChecksText: TextView
    private lateinit var homeValidMetricText: TextView
    private lateinit var homeReviewMetricText: TextView
    private lateinit var homeMissingMetricText: TextView
    private lateinit var homeRecentHistoryEmptyText: TextView
    private lateinit var homeRecentHistoryOneText: TextView
    private lateinit var homeRecentHistoryTwoText: TextView
    private lateinit var homeRecentHistoryThreeText: TextView
    private lateinit var quickVerifyButton: Button
    private lateinit var pickDocumentButton: Button
    private lateinit var scanDocumentButton: Button
    private lateinit var verifyButton: Button
    private lateinit var retryVerifyButton: Button
    private lateinit var filePreviewPanel: LinearLayout
    private lateinit var fileNameText: TextView
    private lateinit var fileMetaText: TextView
    private lateinit var hashText: TextView
    private lateinit var stepUploadText: TextView
    private lateinit var stepAiText: TextView
    private lateinit var stepBlockchainText: TextView
    private lateinit var aiScoreText: TextView
    private lateinit var aiStatusText: TextView
    private lateinit var aiDocTypeText: TextView
    private lateinit var aiOwnerText: TextView
    private lateinit var aiDateIssuerText: TextView
    private lateinit var aiFlagsText: TextView
    private lateinit var extractedText: TextView
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyListText: TextView
    private lateinit var profilePhotoImage: ShapeableImageView
    private lateinit var profileAvatarText: TextView
    private lateinit var profileNameText: TextView
    private lateinit var profileIdText: TextView
    private lateinit var profileTrustBadgeText: TextView
    private lateinit var registryStatusText: TextView
    private lateinit var profileSettingsInfoText: TextView
    private lateinit var profileGoVerifyButton: TextView
    private lateinit var profileGoHistoryButton: TextView
    private lateinit var profileTotalChecksText: TextView
    private lateinit var profileValidChecksText: TextView
    private lateinit var profileReviewChecksText: TextView
    private lateinit var profileDisplayNameInput: EditText
    private lateinit var profilePickPhotoButton: TextView
    private lateinit var profileRemovePhotoButton: TextView
    private lateinit var profileSaveButton: Button
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
    private lateinit var categoryCertificate: LinearLayout
    private lateinit var categoryLetter: LinearLayout
    private lateinit var categoryLogbook: LinearLayout
    private lateinit var categoryTranscript: LinearLayout
    private lateinit var categoryCertificateIcon: ImageView
    private lateinit var categoryLetterIcon: ImageView
    private lateinit var categoryLogbookIcon: ImageView
    private lateinit var categoryTranscriptIcon: ImageView
    private lateinit var categoryCertificateLabel: TextView
    private lateinit var categoryLetterLabel: TextView
    private lateinit var categoryLogbookLabel: TextView
    private lateinit var categoryTranscriptLabel: TextView
    private lateinit var categoryCertificateUnderline: View
    private lateinit var categoryLetterUnderline: View
    private lateinit var categoryLogbookUnderline: View
    private lateinit var categoryTranscriptUnderline: View

    private var selectedUri: Uri? = null
    private var selectedHash: String? = null
    private var selectedFileName: String = ""
    private var adminHash: String? = null
    private var adminFileName: String = ""
    private var userName: String = ""
    private var userId: String = ""
    private var profilePhotoUri: Uri? = null
    private var selectedRole: String = ROLE_STUDENT
    private var activeRole: String = ROLE_STUDENT
    private var validCount = 0
    private var reviewCount = 0
    private var notFoundCount = 0
    private val historyEntries = mutableListOf<String>()
    private val idLocale: Locale = Locale.forLanguageTag("id-ID")

    private enum class HomeCategory {
        CERTIFICATE,
        LETTER,
        LOGBOOK,
        TRANSCRIPT
    }

    private data class HomeSearchMatch(
        val category: HomeCategory?,
        val destinationId: Int?,
        val title: String,
        val description: String
    )

    private companion object {
        const val VALID_NIM = "231011402890"
        const val VALID_PASSWORD = "Rahmat123"
        const val VALID_NAME = "Rahmat Zeka"
        const val ROLE_STUDENT = "student"
        const val ROLE_ADMIN = "admin"
        const val PAGE_SETTINGS = -1001
        const val PAGE_EDIT_PROFILE = -1002
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

    private val profilePhotoPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers only allow temporary access.
            }
            profilePhotoUri = uri
            getPreferences(MODE_PRIVATE).edit()
                .putString("photoUri", uri.toString())
                .apply()
            updateProfileUi()
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
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
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
        roleStudentButton = findViewById(R.id.roleStudentButton)
        roleAdminButton = findViewById(R.id.roleAdminButton)
        topLogoText = findViewById(R.id.topLogoText)
        mainTitleText = findViewById(R.id.mainTitleText)
        mainSubtitleText = findViewById(R.id.mainSubtitleText)
        userBadgeText = findViewById(R.id.userBadgeText)
        profileSettingsButton = findViewById(R.id.profileSettingsButton)
        profileEditButton = findViewById(R.id.profileEditButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        homePage = findViewById(R.id.homePage)
        verifyPage = findViewById(R.id.verifyPage)
        adminPage = findViewById(R.id.adminPage)
        historyPage = findViewById(R.id.historyPage)
        profilePage = findViewById(R.id.profilePage)
        settingsPage = findViewById(R.id.settingsPage)
        editProfilePage = findViewById(R.id.editProfilePage)
        homeSearchInput = findViewById(R.id.homeSearchInput)
        homeSearchActionButton = findViewById(R.id.homeSearchActionButton)
        homeSearchFeedbackText = findViewById(R.id.homeSearchFeedbackText)
        homeHeroTitleText = findViewById(R.id.homeHeroTitleText)
        homeHeroSubtitleText = findViewById(R.id.homeHeroSubtitleText)
        statVerifiedText = findViewById(R.id.statVerifiedText)
        statReviewText = findViewById(R.id.statReviewText)
        homeLastStatusText = findViewById(R.id.homeLastStatusText)
        homeQuickInsightText = findViewById(R.id.homeQuickInsightText)
        homeRegistryStatusText = findViewById(R.id.homeRegistryStatusText)
        homeShortcutScanButton = findViewById(R.id.homeShortcutScanButton)
        homeShortcutPickButton = findViewById(R.id.homeShortcutPickButton)
        homeShortcutHistoryButton = findViewById(R.id.homeShortcutHistoryButton)
        homeTotalChecksText = findViewById(R.id.homeTotalChecksText)
        homeValidMetricText = findViewById(R.id.homeValidMetricText)
        homeReviewMetricText = findViewById(R.id.homeReviewMetricText)
        homeMissingMetricText = findViewById(R.id.homeMissingMetricText)
        homeRecentHistoryEmptyText = findViewById(R.id.homeRecentHistoryEmptyText)
        homeRecentHistoryOneText = findViewById(R.id.homeRecentHistoryOneText)
        homeRecentHistoryTwoText = findViewById(R.id.homeRecentHistoryTwoText)
        homeRecentHistoryThreeText = findViewById(R.id.homeRecentHistoryThreeText)
        quickVerifyButton = findViewById(R.id.quickVerifyButton)
        pickDocumentButton = findViewById(R.id.pickDocumentButton)
        scanDocumentButton = findViewById(R.id.scanDocumentButton)
        verifyButton = findViewById(R.id.verifyButton)
        retryVerifyButton = findViewById(R.id.retryVerifyButton)
        filePreviewPanel = findViewById(R.id.filePreviewPanel)
        fileNameText = findViewById(R.id.fileNameText)
        fileMetaText = findViewById(R.id.fileMetaText)
        hashText = findViewById(R.id.hashText)
        stepUploadText = findViewById(R.id.stepUploadText)
        stepAiText = findViewById(R.id.stepAiText)
        stepBlockchainText = findViewById(R.id.stepBlockchainText)
        aiScoreText = findViewById(R.id.aiScoreText)
        aiStatusText = findViewById(R.id.aiStatusText)
        aiDocTypeText = findViewById(R.id.aiDocTypeText)
        aiOwnerText = findViewById(R.id.aiOwnerText)
        aiDateIssuerText = findViewById(R.id.aiDateIssuerText)
        aiFlagsText = findViewById(R.id.aiFlagsText)
        extractedText = findViewById(R.id.extractedText)
        resultText = findViewById(R.id.resultText)
        progressBar = findViewById(R.id.progressBar)
        historyListText = findViewById(R.id.historyListText)
        profilePhotoImage = findViewById(R.id.profilePhotoImage)
        profileAvatarText = findViewById(R.id.profileAvatarText)
        profileNameText = findViewById(R.id.profileNameText)
        profileIdText = findViewById(R.id.profileIdText)
        profileTrustBadgeText = findViewById(R.id.profileTrustBadgeText)
        registryStatusText = findViewById(R.id.registryStatusText)
        profileSettingsInfoText = findViewById(R.id.profileSettingsInfoText)
        profileGoVerifyButton = findViewById(R.id.profileGoVerifyButton)
        profileGoHistoryButton = findViewById(R.id.profileGoHistoryButton)
        profileTotalChecksText = findViewById(R.id.profileTotalChecksText)
        profileValidChecksText = findViewById(R.id.profileValidChecksText)
        profileReviewChecksText = findViewById(R.id.profileReviewChecksText)
        profileDisplayNameInput = findViewById(R.id.profileDisplayNameInput)
        profilePickPhotoButton = findViewById(R.id.profilePickPhotoButton)
        profileRemovePhotoButton = findViewById(R.id.profileRemovePhotoButton)
        profileSaveButton = findViewById(R.id.profileSaveButton)
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
        categoryCertificate = findViewById(R.id.categoryCertificate)
        categoryLetter = findViewById(R.id.categoryLetter)
        categoryLogbook = findViewById(R.id.categoryLogbook)
        categoryTranscript = findViewById(R.id.categoryTranscript)
        categoryCertificateIcon = findViewById(R.id.categoryCertificateIcon)
        categoryLetterIcon = findViewById(R.id.categoryLetterIcon)
        categoryLogbookIcon = findViewById(R.id.categoryLogbookIcon)
        categoryTranscriptIcon = findViewById(R.id.categoryTranscriptIcon)
        categoryCertificateLabel = findViewById(R.id.categoryCertificateLabel)
        categoryLetterLabel = findViewById(R.id.categoryLetterLabel)
        categoryLogbookLabel = findViewById(R.id.categoryLogbookLabel)
        categoryTranscriptLabel = findViewById(R.id.categoryTranscriptLabel)
        categoryCertificateUnderline = findViewById(R.id.categoryCertificateUnderline)
        categoryLetterUnderline = findViewById(R.id.categoryLetterUnderline)
        categoryLogbookUnderline = findViewById(R.id.categoryLogbookUnderline)
        categoryTranscriptUnderline = findViewById(R.id.categoryTranscriptUnderline)
    }

    private fun bindActions() {
        roleStudentButton.setOnClickListener { selectRole(ROLE_STUDENT) }
        roleAdminButton.setOnClickListener { selectRole(ROLE_ADMIN) }
        loginIdInput.setOnFocusChangeListener { view, hasFocus -> if (hasFocus) scrollLoginTo(view) }
        loginPasswordInput.setOnFocusChangeListener { view, hasFocus -> if (hasFocus) scrollLoginTo(view) }
        loginButton.setOnClickListener { login() }
        profileSettingsButton.setOnClickListener { showProfileSettingsPanel() }
        profileEditButton.setOnClickListener { showProfileEditPanel() }
        profilePhotoImage.setOnClickListener { pickProfilePhoto() }
        profileAvatarText.setOnClickListener { pickProfilePhoto() }
        profileGoVerifyButton.setOnClickListener { bottomNavigation.selectedItemId = R.id.nav_verify }
        profileGoHistoryButton.setOnClickListener { bottomNavigation.selectedItemId = R.id.nav_history }
        profilePickPhotoButton.setOnClickListener { pickProfilePhoto() }
        profileRemovePhotoButton.setOnClickListener { removeProfilePhoto() }
        profileSaveButton.setOnClickListener { saveProfileEdits() }
        homeSearchActionButton.setOnClickListener { performHomeSearch() }
        homeSearchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performHomeSearch()
                true
            } else {
                false
            }
        }
        homeSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(text: Editable?) {
                updateHomeSearchPreview(text?.toString().orEmpty())
            }
        })
        quickVerifyButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
        }
        homeShortcutScanButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
            verifyCamera.launch(null)
        }
        homeShortcutPickButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
            documentPicker.launch(arrayOf("application/pdf", "image/*", "text/*"))
        }
        homeShortcutHistoryButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_history
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
        retryVerifyButton.setOnClickListener {
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
        categoryCertificate.setOnClickListener { openHomeCategory(HomeCategory.CERTIFICATE) }
        categoryLetter.setOnClickListener { openHomeCategory(HomeCategory.LETTER) }
        categoryLogbook.setOnClickListener { openHomeCategory(HomeCategory.LOGBOOK) }
        categoryTranscript.setOnClickListener { openHomeCategory(HomeCategory.TRANSCRIPT) }
        findViewById<LinearLayout>(R.id.shortcutVerify).setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
        }
        findViewById<LinearLayout>(R.id.shortcutScan).setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
            verifyCamera.launch(null)
        }
        findViewById<LinearLayout>(R.id.shortcutAdmin).setOnClickListener {
            if (activeRole == ROLE_ADMIN) {
                bottomNavigation.selectedItemId = R.id.nav_admin
            } else {
                homeSearchFeedbackText.visibility = View.VISIBLE
                homeSearchFeedbackText.text = "Menu admin hanya tersedia saat login dengan role Admin."
            }
        }
        findViewById<LinearLayout>(R.id.shortcutHistory).setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_history
        }
        findViewById<LinearLayout>(R.id.shortcutProfile).setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_profile
        }
        logoutButton.setOnClickListener {
            getPreferences(MODE_PRIVATE).edit().clear().apply()
            selectedHash = null
            selectedRole = ROLE_STUDENT
            activeRole = ROLE_STUDENT
            updateRoleButtons()
            loginContainer.visibility = View.VISIBLE
            appContainer.visibility = View.GONE
        }
        bottomNavigation.setOnItemSelectedListener { item ->
            showPage(item.itemId)
            true
        }
    }

    private fun openHomeCategory(category: HomeCategory) {
        selectHomeCategory(category)
        bottomNavigation.selectedItemId = R.id.nav_verify
        resultText.text = when (category) {
            HomeCategory.CERTIFICATE -> "Kategori Sertifikat dipilih. Pilih atau scan sertifikat kampus untuk memulai verifikasi."
            HomeCategory.LETTER -> "Kategori Surat dipilih. Pilih atau scan surat resmi untuk memulai verifikasi."
            HomeCategory.LOGBOOK -> "Kategori Logbook dipilih. Pilih atau scan logbook untuk memulai verifikasi."
            HomeCategory.TRANSCRIPT -> "Kategori Transkrip dipilih. Pilih atau scan transkrip atau dokumen nilai untuk memulai verifikasi."
        }
    }

    private fun restoreSession() {
        val prefs = getPreferences(MODE_PRIVATE)
        userName = prefs.getString("name", "") ?: ""
        userId = prefs.getString("id", "") ?: ""
        activeRole = prefs.getString("role", ROLE_STUDENT) ?: ROLE_STUDENT
        profilePhotoUri = prefs.getString("photoUri", null)?.let { Uri.parse(it) }
        selectedRole = activeRole
        updateRoleButtons()

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
        activeRole = selectedRole
        getPreferences(MODE_PRIVATE).edit()
            .putString("name", userName)
            .putString("id", userId)
            .putString("role", activeRole)
            .apply()

        loginErrorText.visibility = View.GONE
        showApp()
    }

    private fun showApp() {
        loginContainer.visibility = View.GONE
        appContainer.visibility = View.VISIBLE
        bottomNavigation.menu.findItem(R.id.nav_admin).isVisible = activeRole == ROLE_ADMIN
        updateProfileUi()
        hideProfilePanels()
        selectHomeCategory(HomeCategory.CERTIFICATE)
        updateHome()
        val firstPage = if (activeRole == ROLE_ADMIN) R.id.nav_admin else R.id.nav_home
        bottomNavigation.selectedItemId = firstPage
        showPage(firstPage)
    }

    private fun selectRole(role: String) {
        selectedRole = role
        updateRoleButtons()
    }

    private fun selectHomeCategory(category: HomeCategory) {
        val activeColor = getColor(R.color.validin_text)
        val inactiveColor = getColor(R.color.validin_muted)

        val items = listOf(
            HomeCategory.CERTIFICATE to Triple(categoryCertificateIcon, categoryCertificateLabel, categoryCertificateUnderline),
            HomeCategory.LETTER to Triple(categoryLetterIcon, categoryLetterLabel, categoryLetterUnderline),
            HomeCategory.LOGBOOK to Triple(categoryLogbookIcon, categoryLogbookLabel, categoryLogbookUnderline),
            HomeCategory.TRANSCRIPT to Triple(categoryTranscriptIcon, categoryTranscriptLabel, categoryTranscriptUnderline)
        )

        items.forEach { (itemCategory, views) ->
            val selected = itemCategory == category
            views.first.setColorFilter(if (selected) activeColor else inactiveColor)
            views.second.setTextColor(if (selected) activeColor else inactiveColor)
            views.third.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }

        val title: String
        val subtitle: String
        when (category) {
            HomeCategory.CERTIFICATE -> {
                title = "Sertifikat Kampus"
                subtitle = "Validasi sertifikat seminar, lomba, pelatihan, dan kepanitiaan."
            }
            HomeCategory.LETTER -> {
                title = "Surat Resmi"
                subtitle = "Cek surat tugas, surat aktif kuliah, dan dokumen administrasi."
            }
            HomeCategory.LOGBOOK -> {
                title = "Logbook Digital"
                subtitle = "Pastikan catatan magang atau bimbingan belum dimanipulasi."
            }
            HomeCategory.TRANSCRIPT -> {
                title = "Transkrip & Nilai"
                subtitle = "Cocokkan fingerprint dokumen akademik sebelum digunakan."
            }
        }

        homeHeroTitleText.text = title
        homeHeroSubtitleText.text = subtitle
    }

    private fun performHomeSearch() {
        val query = homeSearchInput.text.toString()
        val match = resolveHomeSearch(query)

        if (match == null) {
            homeSearchFeedbackText.visibility = View.VISIBLE
            homeSearchFeedbackText.text =
                "Ketik contoh: sertifikat, surat tugas, logbook magang, transkrip, scan, riwayat, atau profil."
            return
        }

        match.category?.let { selectHomeCategory(it) }
        homeSearchFeedbackText.visibility = View.VISIBLE
        homeSearchFeedbackText.text = "${match.title}\n${match.description}"
        match.destinationId?.let { bottomNavigation.selectedItemId = it }
    }

    private fun updateHomeSearchPreview(query: String) {
        val match = resolveHomeSearch(query)
        if (query.isBlank()) {
            homeSearchFeedbackText.visibility = View.GONE
            return
        }

        homeSearchFeedbackText.visibility = View.VISIBLE
        if (match == null) {
            homeSearchFeedbackText.text = "Coba kata kunci: sertifikat, surat, logbook, transkrip, nilai, scan, atau riwayat."
        } else {
            homeSearchFeedbackText.text = "${match.title}\n${match.description}"
        }
    }

    private fun resolveHomeSearch(query: String): HomeSearchMatch? {
        val lower = query.trim().lowercase(Locale.ROOT)
        if (lower.isBlank()) return null

        return when {
            lower.anyKeyword("sertifikat", "seminar", "lomba", "pelatihan", "kepanitiaan") -> {
                HomeSearchMatch(
                    category = HomeCategory.CERTIFICATE,
                    destinationId = null,
                    title = "Sertifikat Kampus",
                    description = "Kategori sertifikat dipilih. Tekan Mulai verifikasi untuk cek fingerprint dan registry."
                )
            }
            lower.anyKeyword("surat", "tugas", "aktif kuliah", "administrasi") -> {
                HomeSearchMatch(
                    category = HomeCategory.LETTER,
                    destinationId = null,
                    title = "Surat Resmi",
                    description = "Kategori surat dipilih untuk surat tugas, surat aktif kuliah, dan dokumen administrasi."
                )
            }
            lower.anyKeyword("logbook", "magang", "bimbingan", "catatan") -> {
                HomeSearchMatch(
                    category = HomeCategory.LOGBOOK,
                    destinationId = null,
                    title = "Logbook Digital",
                    description = "Kategori logbook dipilih untuk catatan magang atau bimbingan."
                )
            }
            lower.anyKeyword("transkrip", "nilai", "khs", "ipk") -> {
                HomeSearchMatch(
                    category = HomeCategory.TRANSCRIPT,
                    destinationId = null,
                    title = "Transkrip & Nilai",
                    description = "Kategori transkrip dipilih untuk dokumen akademik dan nilai."
                )
            }
            lower.anyKeyword("verifikasi", "verify", "cek", "scan", "upload", "pilih dokumen") -> {
                HomeSearchMatch(
                    category = null,
                    destinationId = R.id.nav_verify,
                    title = "Buka Verifikasi",
                    description = "Membuka halaman untuk pilih dokumen atau scan kamera."
                )
            }
            lower.anyKeyword("riwayat", "history", "hasil") -> {
                HomeSearchMatch(
                    category = null,
                    destinationId = R.id.nav_history,
                    title = "Buka Riwayat",
                    description = "Membuka daftar hasil pemeriksaan dokumen selama sesi ini."
                )
            }
            lower.anyKeyword("profil", "profile", "akun") -> {
                HomeSearchMatch(
                    category = null,
                    destinationId = R.id.nav_profile,
                    title = "Buka Profil",
                    description = "Membuka informasi akun dan status registry."
                )
            }
            lower.anyKeyword("admin", "terbit", "register", "daftar") && activeRole == ROLE_ADMIN -> {
                HomeSearchMatch(
                    category = null,
                    destinationId = R.id.nav_admin,
                    title = "Buka Admin",
                    description = "Membuka halaman penerbitan dokumen ke registry kampus."
                )
            }
            lower.anyKeyword("admin", "terbit", "register", "daftar") -> {
                HomeSearchMatch(
                    category = null,
                    destinationId = null,
                    title = "Akses Admin",
                    description = "Menu admin hanya tersedia saat login dengan role Admin."
                )
            }
            else -> null
        }
    }

    private fun updateRoleButtons() {
        val selectedColor = ColorStateList.valueOf(getColor(R.color.validin_primary))
        val unselectedColor = ColorStateList.valueOf(getColor(R.color.white))

        roleStudentButton.backgroundTintList =
            if (selectedRole == ROLE_STUDENT) selectedColor else unselectedColor
        roleStudentButton.setTextColor(
            getColor(if (selectedRole == ROLE_STUDENT) R.color.white else R.color.validin_text)
        )

        roleAdminButton.backgroundTintList =
            if (selectedRole == ROLE_ADMIN) selectedColor else unselectedColor
        roleAdminButton.setTextColor(
            getColor(if (selectedRole == ROLE_ADMIN) R.color.white else R.color.validin_text)
        )
    }

    private fun showPage(itemId: Int) {
        homePage.visibility = if (itemId == R.id.nav_home) View.VISIBLE else View.GONE
        verifyPage.visibility = if (itemId == R.id.nav_verify) View.VISIBLE else View.GONE
        adminPage.visibility = if (itemId == R.id.nav_admin) View.VISIBLE else View.GONE
        historyPage.visibility = if (itemId == R.id.nav_history) View.VISIBLE else View.GONE
        profilePage.visibility = if (itemId == R.id.nav_profile) View.VISIBLE else View.GONE
        settingsPage.visibility = if (itemId == PAGE_SETTINGS) View.VISIBLE else View.GONE
        editProfilePage.visibility = if (itemId == PAGE_EDIT_PROFILE) View.VISIBLE else View.GONE
        val isProfileArea = itemId == R.id.nav_profile || itemId == PAGE_SETTINGS || itemId == PAGE_EDIT_PROFILE
        val isProfileRoot = itemId == R.id.nav_profile
        topLogoText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        userBadgeText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        mainSubtitleText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        profileSettingsButton.visibility = if (isProfileRoot) View.VISIBLE else View.GONE
        profileEditButton.visibility = if (isProfileRoot) View.VISIBLE else View.GONE
        mainTitleText.textSize = if (isProfileArea) 26f else 22f

        when (itemId) {
            PAGE_SETTINGS -> {
                mainTitleText.text = "Pengaturan"
                mainSubtitleText.text = "Akun dan aplikasi"
            }
            PAGE_EDIT_PROFILE -> {
                mainTitleText.text = "Edit Profil"
                mainSubtitleText.text = "Nama dan foto"
            }
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

    private fun updateProfileUi() {
        val initials = initials(userName)
        userBadgeText.text = initials
        profileAvatarText.text = initials
        val photoUri = profilePhotoUri
        if (photoUri != null) {
            try {
                profilePhotoImage.setImageURI(photoUri)
                profilePhotoImage.visibility = View.VISIBLE
                profileAvatarText.visibility = View.GONE
            } catch (_: Throwable) {
                profilePhotoUri = null
                profilePhotoImage.visibility = View.GONE
                profileAvatarText.visibility = View.VISIBLE
            }
        } else {
            profilePhotoImage.visibility = View.GONE
            profileAvatarText.visibility = View.VISIBLE
        }
        profileNameText.text = userName
        profileIdText.text = if (activeRole == ROLE_ADMIN) "Admin kampus - $userId" else "Mahasiswa - $userId"
        profileTrustBadgeText.text = if (activeRole == ROLE_ADMIN) {
            "Verified issuer access"
        } else {
            "Verified student access"
        }
        if (registryConfigured()) {
            registryStatusText.setBackgroundResource(R.drawable.bg_pill_green)
            registryStatusText.setTextColor(getColor(R.color.validin_success))
            registryStatusText.text = "Registry kampus aktif dan siap digunakan"
        } else {
            registryStatusText.setBackgroundResource(R.drawable.bg_pill_orange)
            registryStatusText.setTextColor(getColor(R.color.validin_warning))
            registryStatusText.text = "Registry belum dikonfigurasi"
        }
        val totalCount = validCount + reviewCount + notFoundCount
        profileTotalChecksText.text = "$totalCount\nTotal"
        profileValidChecksText.text = "$validCount\nValid"
        profileReviewChecksText.text = "${reviewCount + notFoundCount}\nReview"
        profileSettingsInfoText.text = listOf(
            "Akun: $userName",
            "Role: ${if (activeRole == ROLE_ADMIN) "Admin kampus" else "Mahasiswa"}",
            "NIM/ID: $userId",
            "Registry: ${if (registryConfigured()) "aktif" else "belum dikonfigurasi"}",
            "Riwayat sesi: ${historyEntries.size} pemeriksaan"
        ).joinToString("\n")
    }

    private fun showProfileSettingsPanel() {
        updateProfileUi()
        showPage(PAGE_SETTINGS)
    }

    private fun showProfileEditPanel() {
        profileDisplayNameInput.setText(userName)
        showPage(PAGE_EDIT_PROFILE)
    }

    private fun pickProfilePhoto() {
        profilePhotoPicker.launch(arrayOf("image/*"))
    }

    private fun removeProfilePhoto() {
        profilePhotoUri = null
        getPreferences(MODE_PRIVATE).edit().remove("photoUri").apply()
        updateProfileUi()
    }

    private fun hideProfilePanels() {
        settingsPage.visibility = View.GONE
        editProfilePage.visibility = View.GONE
    }

    private fun saveProfileEdits() {
        val updatedName = profileDisplayNameInput.text.toString().trim()
        if (updatedName.isBlank()) {
            profileDisplayNameInput.error = "Nama tidak boleh kosong"
            return
        }

        userName = updatedName
        getPreferences(MODE_PRIVATE).edit()
            .putString("name", userName)
            .putString("id", userId)
            .putString("role", activeRole)
            .putString("photoUri", profilePhotoUri?.toString())
            .apply()

        updateProfileUi()
        showPage(R.id.nav_profile)
    }

    private fun analyzeDocument(uri: Uri, forcedMimeType: String? = null) {
        setBusy(true)
        verifyButton.isEnabled = false
        retryVerifyButton.visibility = View.GONE
        selectedHash = null
        selectedFileName = displayName(uri)
        val mimeType = forcedMimeType ?: contentResolver.getType(uri)
        filePreviewPanel.visibility = View.VISIBLE
        fileNameText.text = "Nama file: $selectedFileName"
        fileMetaText.text = "Ukuran: ${displayFileSize(uri)} | Jenis: ${displayMimeType(mimeType)}"
        hashText.text = "Hash: menghitung..."
        resetAiInspector("AI sedang membaca dokumen...")
        updateVerifySteps(0)
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
                    hashText.text = "Hash: ${shortHash(hash)}"
                    updateVerifySteps(1)
                    verifyButton.isEnabled = true
                }

                inspector.inspect(
                    uri = uri,
                    mimeType = mimeType,
                    onResult = { report ->
                        runOnUiThread {
                            updateAiInspector(report)
                            updateVerifySteps(2)
                            setBusy(false)
                        }
                    },
                    onError = { error ->
                        runOnUiThread {
                            aiScoreText.text = "!"
                            aiScoreText.setBackgroundResource(R.drawable.bg_alert_card)
                            aiScoreText.setTextColor(getColor(R.color.validin_danger))
                            aiStatusText.text = "AI gagal membaca dokumen."
                            aiFlagsText.setBackgroundResource(R.drawable.bg_alert_card)
                            aiFlagsText.setTextColor(getColor(R.color.validin_danger))
                            aiFlagsText.text = "Catatan: ${error.message ?: "OCR gagal diproses."}"
                            setBusy(false)
                        }
                    }
                )
            } catch (error: Throwable) {
                runOnUiThread {
                    hashText.text = "Hash: gagal dihitung"
                    resetAiInspector(error.message ?: "Dokumen gagal dianalisis.")
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
        val apiUrl = adminApiUrl()

        if (hash == null) {
            adminResultText.text = "Pilih atau scan dokumen dulu."
            return
        }

        if (!apiUrl.startsWith("http")) {
            adminResultText.text = "Server admin belum siap. Jalankan `npm run admin:server` di laptop/server admin."
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
            retryVerifyButton.visibility = View.VISIBLE
            return
        }

        setBusy(true)
        retryVerifyButton.visibility = View.GONE
        updateVerifySteps(3)
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
                    updateProfileUi()
                    retryVerifyButton.visibility = View.VISIBLE
                    setBusy(false)
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    resultText.text = "Verifikasi gagal: ${error.message}"
                    reviewCount += 1
                    updateHome()
                    updateProfileUi()
                    retryVerifyButton.visibility = View.VISIBLE
                    setBusy(false)
                }
            }
        }.start()
    }

    private fun resetAiInspector(message: String) {
        aiScoreText.text = "--"
        aiScoreText.setBackgroundResource(R.drawable.bg_modern_chip)
        aiScoreText.setTextColor(getColor(R.color.validin_primary))
        aiStatusText.text = message
        aiDocTypeText.text = "Jenis dokumen: -"
        aiOwnerText.text = "Pemilik: -\nNIM/NPM/ID: -"
        aiDateIssuerText.text = "Tanggal: -\nPenerbit: -"
        aiFlagsText.setBackgroundResource(R.drawable.bg_pill_orange)
        aiFlagsText.setTextColor(getColor(R.color.validin_warning))
        aiFlagsText.text = "Catatan: belum ada dokumen dibaca."
        extractedText.text = ""
    }

    private fun updateAiInspector(report: AiReport) {
        val status = when {
            report.score >= 80 -> "Risiko rendah"
            report.score >= 55 -> "Perlu review admin"
            else -> "Risiko tinggi"
        }

        aiScoreText.text = "${report.score}\n/100"
        when {
            report.score >= 80 -> {
                aiScoreText.setBackgroundResource(R.drawable.bg_pill_green)
                aiScoreText.setTextColor(getColor(R.color.validin_success))
            }
            report.score >= 55 -> {
                aiScoreText.setBackgroundResource(R.drawable.bg_pill_orange)
                aiScoreText.setTextColor(getColor(R.color.validin_warning))
            }
            else -> {
                aiScoreText.setBackgroundResource(R.drawable.bg_alert_card)
                aiScoreText.setTextColor(getColor(R.color.validin_danger))
            }
        }

        aiStatusText.text = "$status berdasarkan hasil OCR."
        aiDocTypeText.text = "Jenis dokumen: ${report.docType}"
        aiOwnerText.text = "Pemilik: ${report.subjectName ?: "-"}\nNIM/NPM/ID: ${report.subjectId ?: "-"}"
        aiDateIssuerText.text = "Tanggal: ${report.detectedDate ?: "-"}\nPenerbit: ${report.issuerHint ?: "-"}"

        if (report.flags.isEmpty()) {
            aiFlagsText.setBackgroundResource(R.drawable.bg_pill_green)
            aiFlagsText.setTextColor(getColor(R.color.validin_success))
            aiFlagsText.text = "Catatan: tidak ada anomali utama terdeteksi."
        } else {
            aiFlagsText.setBackgroundResource(R.drawable.bg_pill_orange)
            aiFlagsText.setTextColor(getColor(R.color.validin_warning))
            aiFlagsText.text = "Catatan: ${report.flags.joinToString("; ")}"
        }
        extractedText.text = "Teks terbaca:\n${report.extractedText}"
    }

    private fun updateVerifySteps(completedStage: Int) {
        val steps = listOf(stepUploadText, stepAiText, stepBlockchainText)
        steps.forEachIndexed { index, view ->
            val done = completedStage >= index + 1
            view.setBackgroundResource(if (done) R.drawable.bg_yellow_chip else R.drawable.bg_login_dark_chip)
            view.setTextColor(getColor(if (done) R.color.validin_primary_dark else R.color.white))
        }
    }

    private fun recordHistory(result: VerificationResult, formatted: String) {
        when {
            result.exists && result.active -> validCount += 1
            !result.exists -> notFoundCount += 1
            else -> reviewCount += 1
        }

        val status = when {
            result.exists && result.active -> "Valid"
            !result.exists -> "Tidak ditemukan"
            else -> "Perlu review"
        }
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
        val totalCount = validCount + reviewCount + notFoundCount
        statVerifiedText.text = "$validCount Valid"
        statReviewText.text = "${reviewCount + notFoundCount} Review"
        homeTotalChecksText.text = "$totalCount pemeriksaan"
        homeValidMetricText.text = "$validCount\nValid"
        homeReviewMetricText.text = "$reviewCount\nReview"
        homeMissingMetricText.text = "$notFoundCount\nTidak ada"

        if (registryConfigured()) {
            homeRegistryStatusText.setBackgroundResource(R.drawable.bg_pill_green)
            homeRegistryStatusText.setTextColor(getColor(R.color.validin_success))
            homeRegistryStatusText.text = "Registry aktif dan siap mengecek blockchain"
        } else {
            homeRegistryStatusText.setBackgroundResource(R.drawable.bg_pill_orange)
            homeRegistryStatusText.setTextColor(getColor(R.color.validin_warning))
            homeRegistryStatusText.text = "Registry belum dikonfigurasi"
        }

        homeQuickInsightText.text = if (registryConfigured()) {
            "Registry aktif. Verifikasi terakhir: ${if (historyEntries.isEmpty()) "belum ada" else historyEntries.first().lineSequence().firstOrNull().orEmpty()}."
        } else {
            "Registry belum dikonfigurasi. Kamu tetap bisa mencoba OCR, tetapi hasil blockchain belum bisa dicek."
        }

        updateRecentHistoryCards()
        if (historyEntries.isEmpty()) {
            homeLastStatusText.text = "Belum ada dokumen yang diverifikasi."
        }
    }

    private fun updateRecentHistoryCards() {
        val recentViews = listOf(
            homeRecentHistoryOneText,
            homeRecentHistoryTwoText,
            homeRecentHistoryThreeText
        )

        homeRecentHistoryEmptyText.visibility =
            if (historyEntries.isEmpty()) View.VISIBLE else View.GONE

        recentViews.forEachIndexed { index, view ->
            val entry = historyEntries.getOrNull(index)
            if (entry == null) {
                view.visibility = View.GONE
            } else {
                view.text = entry
                view.visibility = View.VISIBLE
            }
        }
    }

    private fun scrollLoginTo(view: View) {
        loginContainer.postDelayed({
            loginContainer.smoothScrollTo(0, view.bottom)
        }, 180)
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

    private fun displayFileSize(uri: Uri): String {
        val size = if (uri.scheme == "file") {
            File(uri.path ?: "").length()
        } else {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val index = it.getColumnIndex(OpenableColumns.SIZE)
                if (index >= 0 && it.moveToFirst()) it.getLong(index) else -1L
            } ?: -1L
        }

        if (size <= 0) return "-"
        val kb = size / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format(Locale.ROOT, "%.1f MB", mb)
            kb >= 1 -> String.format(Locale.ROOT, "%.0f KB", kb)
            else -> "$size B"
        }
    }

    private fun displayMimeType(mimeType: String?): String {
        return when {
            mimeType == "application/pdf" -> "PDF"
            mimeType == "image/jpeg" -> "Gambar JPEG"
            mimeType == "image/png" -> "Gambar PNG"
            mimeType?.startsWith("image/") == true -> "Gambar"
            mimeType?.startsWith("text/") == true -> "Teks"
            mimeType.isNullOrBlank() -> "Tidak diketahui"
            else -> mimeType
        }
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

    private fun adminApiUrl(): String {
        val configured = BuildConfig.VALIDIN_ADMIN_API_URL.trim().trimEnd('/')
        return configured.ifBlank { "http://10.0.2.2:8787" }
    }

    private fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }.ifBlank { "VI" }
    }

    private fun shortAddress(address: String): String {
        if (address.length < 12) return address
        return "${address.take(6)}...${address.takeLast(4)}"
    }

    private fun shortHash(hash: String): String {
        if (hash.length < 24) return hash
        return "${hash.take(14)}...${hash.takeLast(10)}"
    }

    private fun String.anyKeyword(vararg keywords: String): Boolean {
        return keywords.any { contains(it) }
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
