plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.liulishuo.okdownload.filedownloader"
    compileSdk = 34

    defaultConfig {
        minSdk = 19

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.robolectric)
    testImplementation(libs.assertj)

    testImplementation(project(":okdownload"))

    implementation(project(":okdownload"))
    implementation(project(":okhttp"))
    implementation(libs.okhttp3)
    implementation(libs.annotation)
}
