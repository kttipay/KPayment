plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    wasmJs {
        outputModuleName.set("kpayment-sample-web")
        browser {
            commonWebpackConfig {
                outputFileName = "kpayment-sample-web.js"
            }
        }
        binaries.executable()
    }

    js(IR) {
        outputModuleName.set("kpayment-sample-web")
        browser {
            commonWebpackConfig {
                outputFileName = "kpayment-sample-web.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.cedar)
            implementation(projects.paymentCore)
            implementation(projects.paymentWeb)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}
