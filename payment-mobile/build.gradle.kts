import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    androidLibrary {
        namespace = "com.kttipay.payment.mobile"
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
    ).forEach { iosTarget ->
        iosTarget.binaries.framework { isStatic = true }
        swiftExport {
            moduleName = "PaymentMobile"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.paymentCore)

            implementation(libs.jetbrains.runtime)
            implementation(libs.jetbrains.foundation)
            implementation(libs.material3)
            implementation(libs.jetbrains.ui)
            implementation(libs.jetbrains.ui.tooling.preview)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.wallet)
            implementation(libs.compose.pay.button)

            implementation(libs.ui.tooling)
            implementation(libs.ui.tooling.preview)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
        }

        iosMain.dependencies {}
    }
}

dependencies {
    androidRuntimeClasspath(libs.jetbrains.ui.tooling)
}

ktlint {
    version.set("1.8.0")
}

// Publishing your Kotlin Multiplatform library to Maven Central
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("com.kttipay", "kpayment-mobile", libs.versions.appVersionName.get())

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

