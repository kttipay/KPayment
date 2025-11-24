import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    androidTarget()

    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(projects.paymentCore)
//            implementation(projects.common)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

//            implementation(libs.kotlinx.serialization)
//            implementation(libs.kotlinx.coroutines.core)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}


android {
    namespace = "com.kttipay.payment.web"

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