package id.rahmat.newsin

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.appcompat.app.AppCompatDelegate
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
import kotlin.math.max

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
    private lateinit var topBackButton: ImageView
    private lateinit var profileSettingsButton: TextView
    private lateinit var profileEditButton: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var bottomScanButton: ImageView
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
    private lateinit var aboutAppText: TextView
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
    private var welcomeDialog: Dialog? = null
    private var tutorialOverlay: FrameLayout? = null
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

    private data class TutorialStep(
        val pageId: Int,
        val target: () -> View,
        val title: String,
        val message: String,
        val actionHint: String
    )

    private companion object {
        const val SEPOLIA_ETHERSCAN_ADDRESS_URL = "https://sepolia.etherscan.io/address/"
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applySystemBarStyle()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }

        inspector = AiDocumentInspector(this)
        bindViews()
        applyDesignSystem()
        bindActions()
        restoreSession()
    }

    private fun applyDesignSystem() {
        val buttons = listOf(
            loginButton,
            roleStudentButton,
            roleAdminButton,
            quickVerifyButton,
            pickDocumentButton,
            scanDocumentButton,
            verifyButton,
            retryVerifyButton,
            profileSaveButton,
            logoutButton,
            pickAdminDocumentButton,
            scanAdminDocumentButton,
            registerAdminButton
        )
        buttons.forEach { button ->
            button.setAllCaps(true)
            button.letterSpacing = 0.12f
            button.typeface = Typeface.DEFAULT_BOLD
        }

        listOf(
            homeSearchActionButton,
            homeShortcutScanButton,
            homeShortcutPickButton,
            homeShortcutHistoryButton,
            profileGoVerifyButton,
            profileGoHistoryButton,
            profilePickPhotoButton,
            profileRemovePhotoButton
        ).forEach { action ->
            action.letterSpacing = 0.08f
            action.typeface = Typeface.DEFAULT_BOLD
        }

        val greenTint = ColorStateList.valueOf(getColor(R.color.validin_primary))
        progressBar.indeterminateTintList = greenTint
        progressBar.progressTintList = greenTint
        adminProgressBar.indeterminateTintList = greenTint
        adminProgressBar.progressTintList = greenTint
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
        topBackButton = findViewById(R.id.topBackButton)
        profileSettingsButton = findViewById(R.id.profileSettingsButton)
        profileEditButton = findViewById(R.id.profileEditButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomScanButton = findViewById(R.id.bottomScanButton)
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
        resultText.setTextIsSelectable(false)
        resultText.movementMethod = LinkMovementMethod.getInstance()
        resultText.highlightColor = getColor(android.R.color.transparent)
        progressBar = findViewById(R.id.progressBar)
        historyListText = findViewById(R.id.historyListText)
        profilePhotoImage = findViewById(R.id.profilePhotoImage)
        profileAvatarText = findViewById(R.id.profileAvatarText)
        profileNameText = findViewById(R.id.profileNameText)
        profileIdText = findViewById(R.id.profileIdText)
        profileTrustBadgeText = findViewById(R.id.profileTrustBadgeText)
        registryStatusText = findViewById(R.id.registryStatusText)
        profileSettingsInfoText = findViewById(R.id.profileSettingsInfoText)
        aboutAppText = findViewById(R.id.aboutAppText)
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
        topBackButton.setOnClickListener { showPage(R.id.nav_profile) }
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
            welcomeDialog?.dismiss()
            removeTutorialOverlay()
            getPreferences(MODE_PRIVATE).edit().clear().apply()
            selectedHash = null
            selectedRole = ROLE_STUDENT
            activeRole = ROLE_STUDENT
            bottomNavigation.menu.findItem(R.id.nav_admin)?.isVisible = false
            updateRoleButtons()
            loginContainer.visibility = View.VISIBLE
            appContainer.visibility = View.GONE
        }
        bottomNavigation.setOnItemSelectedListener { item ->
            showPage(item.itemId)
            true
        }
        bottomScanButton.setOnClickListener {
            bottomNavigation.selectedItemId = R.id.nav_verify
            verifyCamera.launch(null)
        }
        bottomScanButton.bringToFront()
        setupAboutLink()
        setupBackNavigation()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (tutorialOverlay != null) {
                    finishTutorial()
                    return
                }

                if (settingsPage.visibility == View.VISIBLE || editProfilePage.visibility == View.VISIBLE) {
                    showPage(R.id.nav_profile)
                    return
                }

                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        })
    }

    private fun setupAboutLink() {
        val githubUrl = "https://github.com/rhmatzeka"
        val text = "Tentang ValidIn\n\n" +
            "ValidIn membantu verifikasi dokumen kampus dengan OCR, fingerprint SHA-256, dan registry blockchain. " +
            "Dokumen asli tidak disimpan di blockchain, hanya fingerprint dan metadata hash.\n\n" +
            "Dibuat oleh Rahmat Eka Satria\nGitHub: $githubUrl"
        val start = text.indexOf(githubUrl)
        val span = SpannableString(text)
        span.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = getColor(R.color.validin_primary)
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                }
            },
            start,
            start + githubUrl.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        aboutAppText.text = span
        aboutAppText.movementMethod = LinkMovementMethod.getInstance()
        aboutAppText.highlightColor = getColor(android.R.color.transparent)
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
        showWelcomeTutorial()
    }

    private fun showApp() {
        loginContainer.visibility = View.GONE
        appContainer.visibility = View.VISIBLE
        bottomNavigation.menu.findItem(R.id.nav_admin)?.isVisible = activeRole == ROLE_ADMIN
        updateProfileUi()
        hideProfilePanels()
        selectHomeCategory(HomeCategory.CERTIFICATE)
        updateHome()
        val firstPage = R.id.nav_home
        bottomNavigation.selectedItemId = firstPage
        showPage(firstPage)
    }

    private fun showWelcomeTutorial() {
        welcomeDialog?.dismiss()

        val dialog = Dialog(this)
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedDrawable(getColor(R.color.validin_surface), 8)
            setPadding(dp(20), dp(18), dp(20), dp(18))
        }
        val container = FrameLayout(this).apply {
            setPadding(dp(18), dp(24), dp(18), dp(24))
            addView(card, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ))
        }

        val closeButton = TextView(this).apply {
            text = "X"
            gravity = Gravity.CENTER
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(getColor(R.color.validin_muted))
            background = roundedDrawable(getColor(R.color.validin_background), 500)
            setOnClickListener { dialog.dismiss() }
        }
        container.addView(closeButton, FrameLayout.LayoutParams(dp(38), dp(38), Gravity.TOP or Gravity.RIGHT).apply {
            topMargin = dp(12)
            rightMargin = dp(12)
        })

        card.addView(labelText("VALIDIN", 12f, getColor(R.color.validin_primary), true))
        card.addView(labelText("Selamat datang, $userName", 24f, getColor(R.color.validin_text), true).apply {
            setPadding(0, dp(8), dp(38), 0)
        })
        card.addView(labelText(
            "Aplikasi ini membantu kamu mengecek dokumen kampus lewat AI OCR, fingerprint SHA-256, dan blockchain registry.",
            15f,
            getColor(R.color.validin_muted),
            false
        ).apply {
            setPadding(0, dp(10), 0, 0)
        })
        card.addView(labelText(
            "Tutorial berikut akan menyorot tombol asli di aplikasi dan menjelaskan cara pakainya satu per satu.",
            14f,
            getColor(R.color.validin_text),
            false
        ).apply {
            background = roundedDrawable(getColor(R.color.validin_background), 8)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            (layoutParams as? LinearLayout.LayoutParams)?.topMargin = dp(14)
        })

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(18), 0, 0)
        }
        actions.addView(tutorialButton("Nanti saja", false) { dialog.dismiss() }, LinearLayout.LayoutParams(
            0,
            dp(48),
            1f
        ).apply { rightMargin = dp(10) })
        actions.addView(tutorialButton("Mulai", true) {
            dialog.dismiss()
            showTutorialStep(0)
        }, LinearLayout.LayoutParams(0, dp(48), 1f))
        card.addView(actions)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(container)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        welcomeDialog = dialog
    }

    private fun showTutorialStep(index: Int) {
        val steps = tutorialSteps()
        if (index !in steps.indices) {
            finishTutorial()
            return
        }

        val step = steps[index]
        showTutorialPage(step.pageId)
        scrollTutorialTarget(step)
        findViewById<FrameLayout>(R.id.main).postDelayed({
            renderTutorialOverlay(index)
        }, 360)
    }

    private fun renderTutorialOverlay(index: Int) {
        val steps = tutorialSteps()
        if (index !in steps.indices) return

        val root = findViewById<FrameLayout>(R.id.main)
        val step = steps[index]
        val target = step.target()
        if (target.width == 0 || target.height == 0) {
            root.postDelayed({ renderTutorialOverlay(index) }, 160)
            return
        }

        removeTutorialOverlay()
        val overlay = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#B8000000"))
            isClickable = true
        }
        root.addView(overlay, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        val rootLocation = IntArray(2)
        val targetLocation = IntArray(2)
        root.getLocationOnScreen(rootLocation)
        target.getLocationOnScreen(targetLocation)
        val padding = dp(8)
        val targetLeft = (targetLocation[0] - rootLocation[0] - padding).coerceAtLeast(dp(10))
        val targetTop = (targetLocation[1] - rootLocation[1] - padding).coerceAtLeast(dp(10))
        val targetWidth = (target.width + padding * 2).coerceAtMost(root.width - dp(20))
        val targetHeight = target.height + padding * 2

        val highlight = FrameLayout(this).apply {
            background = roundedDrawable(Color.TRANSPARENT, 8, getColor(R.color.validin_primary), dp(3))
        }
        overlay.addView(highlight, FrameLayout.LayoutParams(targetWidth, targetHeight).apply {
            leftMargin = targetLeft
            topMargin = targetTop
        })

        overlay.addView(labelText("Bagian ini", 12f, getColor(R.color.validin_primary_dark), true).apply {
            gravity = Gravity.CENTER
            background = roundedDrawable(getColor(R.color.validin_yellow), 999)
            setPadding(dp(10), dp(5), dp(10), dp(5))
        }, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            dp(30)
        ).apply {
            leftMargin = targetLeft
            topMargin = max(dp(8), targetTop - dp(34))
        })

        val card = tutorialCard(index, steps.size, step)
        val targetCenterY = targetTop + targetHeight / 2
        val cardParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = dp(16)
            rightMargin = dp(16)
            if (targetCenterY < root.height / 2) {
                gravity = Gravity.BOTTOM
                bottomMargin = dp(24)
            } else {
                gravity = Gravity.TOP
                topMargin = dp(24)
            }
        }
        overlay.addView(card, cardParams)
        tutorialOverlay = overlay
    }

    private fun tutorialCard(index: Int, total: Int, step: TutorialStep): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedDrawable(getColor(R.color.validin_surface), 8)
            setPadding(dp(16), dp(14), dp(16), dp(16))

            val header = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            header.addView(labelText("Langkah ${index + 1}/$total", 12f, getColor(R.color.validin_primary), true), LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            ))
            header.addView(tutorialButton("X", false) { finishTutorial() }, LinearLayout.LayoutParams(dp(42), dp(38)))
            addView(header)

            addView(labelText(step.title, 21f, getColor(R.color.validin_text), true).apply {
                setPadding(0, dp(8), 0, 0)
            })
            addView(labelText(step.message, 14f, getColor(R.color.validin_muted), false).apply {
                setPadding(0, dp(8), 0, 0)
            })
            addView(labelText("Cara pakai: ${step.actionHint}", 14f, getColor(R.color.validin_primary_dark), true).apply {
                background = roundedDrawable(getColor(R.color.validin_background), 8)
                setPadding(dp(12), dp(10), dp(12), dp(10))
            }, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) })

            val actions = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(14), 0, 0)
            }
            actions.addView(tutorialButton("Lewati", false) { finishTutorial() }, LinearLayout.LayoutParams(
                0,
                dp(46),
                1f
            ).apply { rightMargin = dp(8) })
            if (index > 0) {
                actions.addView(tutorialButton("Kembali", false) { showTutorialStep(index - 1) }, LinearLayout.LayoutParams(
                    0,
                    dp(46),
                    1f
                ).apply { rightMargin = dp(8) })
            }
            actions.addView(tutorialButton(if (index == total - 1) "Selesai" else "Lanjut", true) {
                showTutorialStep(index + 1)
            }, LinearLayout.LayoutParams(0, dp(46), 1f))
            addView(actions)
        }
    }

    private fun showTutorialPage(pageId: Int) {
        if (pageId == R.id.nav_home ||
            pageId == R.id.nav_verify ||
            pageId == R.id.nav_history ||
            pageId == R.id.nav_profile ||
            pageId == R.id.nav_admin && activeRole == ROLE_ADMIN
        ) {
            bottomNavigation.selectedItemId = pageId
        } else {
            showPage(pageId)
        }
    }

    private fun scrollTutorialTarget(step: TutorialStep) {
        val scrollView = tutorialScrollView(step.pageId) ?: return
        scrollView.post {
            val scrollLocation = IntArray(2)
            val targetLocation = IntArray(2)
            scrollView.getLocationOnScreen(scrollLocation)
            step.target().getLocationOnScreen(targetLocation)
            val targetY = targetLocation[1] - scrollLocation[1] + scrollView.scrollY - dp(140)
            scrollView.smoothScrollTo(0, max(0, targetY))
        }
    }

    private fun tutorialScrollView(pageId: Int): ScrollView? {
        return when (pageId) {
            R.id.nav_home -> homePage
            R.id.nav_verify -> verifyPage
            R.id.nav_history -> historyPage
            R.id.nav_profile -> profilePage
            R.id.nav_admin -> adminPage
            else -> null
        }
    }

    private fun finishTutorial() {
        removeTutorialOverlay()
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun removeTutorialOverlay() {
        tutorialOverlay?.let { overlay ->
            (overlay.parent as? ViewGroup)?.removeView(overlay)
        }
        tutorialOverlay = null
    }

    private fun tutorialSteps(): List<TutorialStep> {
        val steps = mutableListOf(
            TutorialStep(
                pageId = R.id.nav_home,
                target = { homeSearchInput },
                title = "Cari fitur dari Beranda",
                message = "Kolom ini membantu pengguna langsung menemukan menu verifikasi, scan, riwayat, profil, atau admin tanpa mencari manual.",
                actionHint = "Ketik kata seperti verifikasi, scan, history, profil, atau admin, lalu tekan tombol cari."
            ),
            TutorialStep(
                pageId = R.id.nav_home,
                target = { categoryCertificate },
                title = "Pilih kategori dokumen",
                message = "Kategori ini membantu pengguna menentukan konteks dokumen yang akan diperiksa, misalnya sertifikat, surat, logbook, atau transkrip.",
                actionHint = "Pilih salah satu kategori, lalu aplikasi akan mengarahkan kamu ke halaman verifikasi."
            ),
            TutorialStep(
                pageId = R.id.nav_home,
                target = { quickVerifyButton },
                title = "Mulai verifikasi cepat",
                message = "Tombol ini adalah jalan paling cepat untuk masuk ke alur pemeriksaan dokumen.",
                actionHint = "Tekan tombol ini saat ingin langsung memilih atau scan dokumen."
            ),
            TutorialStep(
                pageId = R.id.nav_verify,
                target = { pickDocumentButton },
                title = "Pilih file dokumen",
                message = "Gunakan tombol ini untuk mengambil PDF, gambar, atau file teks dari perangkat. File asli digital memberi hasil hash paling kuat.",
                actionHint = "Tekan Pilih dokumen, pilih file resmi, lalu tunggu hash dan AI OCR selesai."
            ),
            TutorialStep(
                pageId = R.id.nav_verify,
                target = { scanDocumentButton },
                title = "Scan dokumen fisik",
                message = "Tombol scan membuka kamera untuk membaca dokumen fisik. Ini berguna untuk review cepat, tetapi hasil foto bisa dipengaruhi cahaya dan sudut kamera.",
                actionHint = "Arahkan kamera ke dokumen dengan terang dan lurus agar AI lebih mudah membaca teks."
            ),
            TutorialStep(
                pageId = R.id.nav_verify,
                target = { verifyButton },
                title = "Cek status blockchain",
                message = "Setelah dokumen dipilih dan hash berhasil dihitung, tombol ini mencocokkan fingerprint dokumen ke registry blockchain.",
                actionHint = "Tekan Cek status setelah tombol aktif untuk melihat apakah dokumen valid, dicabut, atau tidak terdaftar."
            ),
            TutorialStep(
                pageId = R.id.nav_verify,
                target = { resultText },
                title = "Baca hasil pemeriksaan",
                message = "Area ini menampilkan status akhir, issuer kampus, kontrak registry, waktu pencatatan, dan link Etherscan jika dokumen ditemukan.",
                actionHint = "Kalau alamat issuer atau kontrak muncul, tekan alamatnya untuk melihat bukti publik di Etherscan."
            ),
            TutorialStep(
                pageId = R.id.nav_history,
                target = { historyListText },
                title = "Cek riwayat verifikasi",
                message = "Setiap pemeriksaan dalam sesi aplikasi akan dirangkum di sini agar pengguna bisa meninjau ulang status dokumen terakhir.",
                actionHint = "Buka halaman Riwayat setelah verifikasi untuk melihat nama dokumen, waktu, dan status ringkasnya."
            ),
            TutorialStep(
                pageId = R.id.nav_profile,
                target = { profileSettingsButton },
                title = "Buka pengaturan akun",
                message = "Tombol ini membuka detail akun, role, NIM/ID, status registry, dan jumlah pemeriksaan dalam sesi aplikasi.",
                actionHint = "Tekan ikon pengaturan saat ingin mengecek konfigurasi akun dan registry."
            ),
            TutorialStep(
                pageId = R.id.nav_profile,
                target = { profileEditButton },
                title = "Edit profil",
                message = "Tombol ini dipakai untuk mengganti nama tampilan dan foto profil pengguna.",
                actionHint = "Tekan edit, ubah nama atau foto, lalu simpan perubahan."
            )
        )

        if (activeRole == ROLE_ADMIN) {
            steps.addAll(
                listOf(
                    TutorialStep(
                        pageId = R.id.nav_admin,
                        target = { pickAdminDocumentButton },
                        title = "Siapkan dokumen resmi",
                        message = "Admin atau issuer kampus memilih dokumen resmi di sini. Aplikasi akan menghitung fingerprint sebelum dokumen didaftarkan.",
                        actionHint = "Pilih file resmi yang final, karena perubahan kecil pada file akan menghasilkan hash berbeda."
                    ),
                    TutorialStep(
                        pageId = R.id.nav_admin,
                        target = { registerAdminButton },
                        title = "Daftarkan ke registry",
                        message = "Tombol ini mengirim fingerprint, metadata, dan subjek dokumen ke API admin untuk dicatat di smart contract.",
                        actionHint = "Pastikan jenis dokumen dan subjek sudah benar, lalu tekan Daftarkan dokumen."
                    )
                )
            )
        }

        return steps
    }

    private fun labelText(textValue: String, sizeSp: Float, color: Int, bold: Boolean): TextView {
        return TextView(this).apply {
            text = textValue
            textSize = sizeSp
            setTextColor(color)
            setLineSpacing(dp(2).toFloat(), 1f)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun tutorialButton(textValue: String, filled: Boolean, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            text = textValue
            gravity = Gravity.CENTER
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.12f
            setTextColor(getColor(if (filled) R.color.validin_primary_dark else R.color.validin_primary))
            background = if (filled) {
                roundedDrawable(getColor(R.color.validin_primary), 500)
            } else {
                roundedDrawable(Color.TRANSPARENT, 500, getColor(R.color.validin_border), dp(1))
            }
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
    }

    private fun roundedDrawable(
        fillColor: Int,
        radiusDp: Int,
        strokeColor: Int? = null,
        strokeWidthPx: Int = 0
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            color = ColorStateList.valueOf(fillColor)
            cornerRadius = dp(radiusDp).toFloat()
            if (strokeColor != null && strokeWidthPx > 0) {
                setStroke(strokeWidthPx, strokeColor)
            }
        }
    }

    private fun selectRole(role: String) {
        selectedRole = role
        updateRoleButtons()
    }

    private fun selectHomeCategory(category: HomeCategory) {
        val activeColor = getColor(R.color.validin_primary)
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
            views.third.setBackgroundColor(activeColor)
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
        val unselectedColor = ColorStateList.valueOf(getColor(R.color.validin_surface))

        roleStudentButton.backgroundTintList =
            if (selectedRole == ROLE_STUDENT) selectedColor else unselectedColor
        roleStudentButton.setTextColor(
            getColor(if (selectedRole == ROLE_STUDENT) R.color.validin_primary_dark else R.color.validin_text)
        )

        roleAdminButton.backgroundTintList =
            if (selectedRole == ROLE_ADMIN) selectedColor else unselectedColor
        roleAdminButton.setTextColor(
            getColor(if (selectedRole == ROLE_ADMIN) R.color.validin_primary_dark else R.color.validin_text)
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
        val isNestedProfilePage = itemId == PAGE_SETTINGS || itemId == PAGE_EDIT_PROFILE
        topLogoText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        userBadgeText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        mainSubtitleText.visibility = if (isProfileArea) View.GONE else View.VISIBLE
        profileSettingsButton.visibility = if (isProfileRoot) View.VISIBLE else View.GONE
        profileEditButton.visibility = if (isProfileRoot) View.VISIBLE else View.GONE
        topBackButton.visibility = if (isNestedProfilePage) View.VISIBLE else View.GONE
        mainTitleText.textSize = if (isProfileArea) 24f else 22f

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
            adminResultText.text = "Alamat server admin belum diisi. Isi VALIDIN_ADMIN_API_URL dengan IP laptop, contoh http://10.241.83.218:8787, lalu build ulang app."
            return
        }

        if (apiUrl.contains("10.0.2.2")) {
            adminResultText.text = "Alamat 10.0.2.2 hanya untuk emulator. Karena kamu pakai HP, isi VALIDIN_ADMIN_API_URL dengan IP laptop di jaringan yang sama."
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
                    resultText.text = formatVerificationResultForDisplay(result)
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
            "Kontrak registry: ${shortAddress(BuildConfig.VALIDIN_CONTRACT_ADDRESS)}",
            "Dicatat pada: ${formatTimestamp(result.issuedAtEpochSeconds)}",
            "Metadata: ${result.metadataHash.take(14)}...${result.metadataHash.takeLast(10)}",
            "Pemilik: ${result.subjectHash.take(14)}...${result.subjectHash.takeLast(10)}"
        ).joinToString("\n")
    }

    private fun formatVerificationResultForDisplay(result: VerificationResult): CharSequence {
        val text = formatVerificationResult(result)
        if (!result.exists) return text

        val span = SpannableString(text)
        addExplorerLink(span, text, shortAddress(result.issuer), result.issuer)
        addExplorerLink(
            span,
            text,
            shortAddress(BuildConfig.VALIDIN_CONTRACT_ADDRESS),
            BuildConfig.VALIDIN_CONTRACT_ADDRESS
        )
        return span
    }

    private fun addExplorerLink(span: SpannableString, text: String, label: String, address: String) {
        val start = text.indexOf(label)
        if (start < 0 || !address.matches(Regex("^0x[0-9a-fA-F]{40}$"))) return

        val url = "$SEPOLIA_ETHERSCAN_ADDRESS_URL$address"
        span.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = getColor(R.color.validin_primary)
                    ds.isUnderlineText = true
                    ds.isFakeBoldText = true
                }
            },
            start,
            start + label.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun updateHome() {
        val totalCount = validCount + reviewCount + notFoundCount
        statVerifiedText.text = "$validCount Valid"
        statReviewText.text = "${reviewCount + notFoundCount} Review"
        homeTotalChecksText.text = "$totalCount pemeriksaan"
        homeValidMetricText.text = metricText(validCount, "Valid")
        homeReviewMetricText.text = metricText(reviewCount, "Review")
        homeMissingMetricText.text = metricText(notFoundCount, "Tidak ada")

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

    private fun metricText(value: Int, label: String): CharSequence {
        val text = "$value\n$label"
        return SpannableString(text).apply {
            setSpan(RelativeSizeSpan(1.18f), 0, value.toString().length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(0.9f), value.toString().length + 1, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun scrollLoginTo(view: View) {
        loginContainer.postDelayed({
            loginContainer.smoothScrollTo(0, view.bottom + dp(132))
        }, 180)
    }

    private fun applySystemBarStyle() {
        window.statusBarColor = getColor(R.color.validin_background)
        window.navigationBarColor = getColor(R.color.validin_background)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
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
        return configured
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
