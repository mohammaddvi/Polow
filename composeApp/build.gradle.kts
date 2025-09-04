import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform) // Or "org.jetbrains.kotlin.multiplatform"
    alias(libs.plugins.composeMultiplatform) // Or "org.jetbrains.compose" if it's a Compose Multiplatform module
    // If this module also directly builds an Android library/app, you might need:
     alias(libs.plugins.androidLibrary) // or androidApplication
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions { // Use compilerOptions here
            jvmTarget.set(JvmTarget.JVM_1_8) // Set the jvmTarget using the new DSL
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
        }
    }
}

android {
    namespace = "com.example.kmpbottomnav"
    compileSdk = 36

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true // Enable Compose for the Android part
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
