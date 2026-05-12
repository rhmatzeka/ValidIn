plugins {
    alias(libs.plugins.android.application)
}

fun loadRootEnv(): Map<String, String> {
    val envFile = rootProject.file(".env")
    if (!envFile.exists()) return emptyMap()

    return envFile.readLines()
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
        .associate {
            val key = it.substringBefore("=").trim()
            val value = it.substringAfter("=").trim().trim('"', '\'')
            key to value
        }
}

fun String.asBuildConfigString(): String {
    val escaped = replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
}

val validInEnv = loadRootEnv()

android {
    namespace = "id.rahmat.newsin"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "id.rahmat.newsin"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "VALIDIN_RPC_URL",
            (validInEnv["SEPOLIA_RPC_URL"] ?: "").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "VALIDIN_CONTRACT_ADDRESS",
            (validInEnv["VALIDIN_CONTRACT_ADDRESS"] ?: "").asBuildConfigString()
        )
        buildConfigField(
            "String",
            "VALIDIN_ADMIN_API_URL",
            (validInEnv["VALIDIN_ADMIN_API_URL"] ?: "").asBuildConfigString()
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.mlkit.text.recognition)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
