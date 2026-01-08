import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.ktlint)
}

kotlin {
    jvmToolchain(libs.versions.javaVersion.get().toInt())

    androidLibrary {
        namespace = "com.kttipay.payment.web"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    js { browser() }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(projects.paymentCore)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

// Publishing your Kotlin Multiplatform library to Maven Central
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("com.kttipay", "kpayment-web", libs.versions.appVersionName.get())

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
            url = libs.versions.libraryUrl.get().toString()
        }
    }
}

ktlint {
    version.set("1.8.0")
}
