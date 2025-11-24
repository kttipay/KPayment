@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    jvm()
    
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    js(IR) {
        browser()
    }

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.cedar)
            api(libs.deci)
        }
    }
}

android {
    namespace = "com.kttipay.payment.core"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

