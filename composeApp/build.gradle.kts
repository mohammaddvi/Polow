import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
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
            implementation(libs.moko.permissions.compose) // Add this line
            implementation(libs.moko.permissions)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            // Image picker bundle
            implementation(libs.bundles.image.picker)
            // Permissions bundle
            implementation(libs.bundles.permissions)

            // Ktor bundle for networking
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.bundles.networking)
            implementation(libs.bundles.image.handling)
        }
        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.bundles.camera)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
}

