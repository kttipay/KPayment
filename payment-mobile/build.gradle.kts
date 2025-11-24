import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    androidTarget {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                jvmTarget.set(JvmTarget.valueOf(libs.versions.jvmVersion.get()))
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework { isStatic = true }
        swiftExport {
            moduleName = "PaymentMobile"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.paymentCore)
//            implementation(projects.common)
            
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            
//            implementation(project.dependencies.platform(libs.koin.bom))
//            implementation(libs.bundles.koin)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.wallet)
            implementation(libs.compose.pay.button)
//            implementation(libs.koin.android)
        }

        iosMain.dependencies {}
    }
}

android {
    namespace = "com.kttipay.payment.mobile"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        create("staging") {
            initWith(getByName("debug"))
            isMinifyEnabled = false
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

