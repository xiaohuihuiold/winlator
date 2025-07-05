plugins {
    alias(libs.plugins.android.application)
}

android {
    compileSdk = 36
    namespace = "com.winlator"

    defaultConfig {
        applicationId = "com.winlator"
        minSdk = 26
        targetSdk = 28
        versionCode = 16
        versionName = "7.1"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )

            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            }
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    ndkVersion = "22.1.7171670"

    externalNativeBuild {
        cmake {
            version = "3.22.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.material)
    implementation(libs.zstd.jni) {
        artifact {
            type = "aar"
        }
    }
    implementation(libs.xz)
    implementation(libs.commons.compress)
}
