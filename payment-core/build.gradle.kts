@file:OptIn(ExperimentalWasmDsl::class)

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.ktlint)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    jvm()

    androidLibrary {
        namespace = "com.kttipay.payment.core"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
        withHostTest {
            isIncludeAndroidResources = true
        }

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.files.add(project.file("proguard-rules.pro"))
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
            api("androidx.compose.runtime:runtime-annotation:1.9.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// Publishing your Kotlin Multiplatform library to Maven Central
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("com.kttipay", "kpayment-core", libs.versions.appVersionName.get())

    pom {
        name = libs.versions.libraryName.get()
        description = libs.versions.libraryDescription.get()
        url = libs.versions.libraryUrl.get()
        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }

        developers {
            developer {
                id = "merkost"
                name = "Konstantin Merenkov"
                email = "kosta0212@gmail.com"
            }

            developer {
                id = "diogocavaiar"
                name = "Diogo Cavaiar"
                email = "cavaiarconsulting@gmail.com"
            }
        }

        scm {
            url = libs.versions.libraryUrl.get()
        }
    }
}

ktlint {
    version.set("1.8.0")
}
