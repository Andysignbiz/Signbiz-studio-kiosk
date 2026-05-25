plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "signbiz.kiosk"
    compileSdk = 36

    defaultConfig {
        applicationId = "signbiz.kiosk"
        minSdk = 22
        targetSdk = 36
        versionCode = 11
        versionName = "0.0.11"
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
    }

    applicationVariants.all {
        val variant = this
        outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = "signbiz-studio-kiosk-${variant.buildType.name}.apk"
            }
    }
}

// Remove duplicate PNG icon files before resource merge.
// Icons are provided as .webp - the .png versions cause duplicate resource errors.
val removeDuplicateIconPngs by tasks.registering {
    doLast {
        val mipmapDirs = listOf("mipmap-mdpi", "mipmap-hdpi", "mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi")
        val pngNames = listOf("ic_launcher.png", "ic_launcher_round.png", "ic_launcher_foreground.png")
        mipmapDirs.forEach { dir ->
            pngNames.forEach { name ->
                val f = File(projectDir, "src/main/res/$dir/$name")
                if (f.exists()) {
                    f.delete()
                    println("Removed duplicate: $dir/$name")
                }
            }
        }
    }
}

afterEvaluate {
    tasks.matching { it.name == "mergeDebugResources" || it.name == "mergeReleaseResources" }.configureEach {
        dependsOn(removeDuplicateIconPngs)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.leanback)
    implementation(libs.androidx.datastore.preferences)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}