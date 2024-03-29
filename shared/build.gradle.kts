import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig")
}

val coroutinesVersion = "1.6.4"
val ktorVersion = "2.1.2"
val sqlDelightVersion = "1.5.4"

kotlin {
    android()
    listOf(
        iosX64(), // If run app in simulator, we need this
        iosArm64(),
        iosSimulatorArm64(), // If run app in simulator, we need this
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            export("dev.icerock.moko:resources:0.20.1")
            export("dev.icerock.moko:graphics:0.9.0") // toUIColor here
        }
    }

    cocoapods {
        summary = "TBIA"
        homepage = "https://github.com/himphen"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            isStatic = true
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-resources:$ktorVersion")
                implementation("io.ktor:ktor-client-encoding:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")

                // https://github.com/InsertKoinIO/koin
                implementation("io.insert-koin:koin-core:3.1.6")

                implementation("dev.gitlive:firebase-auth:1.6.2")
                implementation("dev.gitlive:firebase-database:1.6.2")
                implementation("dev.gitlive:firebase-config:1.6.2")

                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                api("dev.icerock.moko:graphics:0.9.0")
                api("dev.icerock.moko:parcelize:0.8.0")
                api("dev.icerock.moko:resources:0.20.1")

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // https://github.com/AAkira/Napier
                implementation("io.github.aakira:napier:2.6.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

                implementation("com.github.himphen:logger:3.0.1")

                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")

                implementation("com.google.maps.android:maps-ktx:3.3.0")
                implementation("com.google.maps.android:maps-utils-ktx:3.3.0")

                implementation("androidx.preference:preference-ktx:1.2.0")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.ktor:ktor-client-darwin:$ktorVersion")

                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }

    // export correct artifact to use all classes of moko-resources directly from Swift
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).all {
        binaries.withType(org.jetbrains.kotlin.gradle.plugin.mpp.Framework::class.java).all {
            export("dev.icerock.moko:resources:0.20.1")
        }
    }
}

android {
    namespace = "hibernate.v2"
    compileSdk = 31
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
    sourceSets["main"].apply {
        assets.srcDir(File(buildDir, "generated/moko/androidMain/assets"))
        res.srcDir(File(buildDir, "generated/moko/androidMain/res"))
    }
}

sqldelight {
    database("MyDatabase") {
        packageName = "hibernate.v2.database"
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "hibernate.v2"
    iosBaseLocalizationRegion = "zh-TW"
    disableStaticFrameworkWarning = true
}

buildkonfig {
    packageName = "hibernate.v2.tbia"

    val configFilename = rootDir.toString() + File.separator + "secrets.properties"
    val properties: Properties = loadProperties(configFilename)

    defaultConfigs {
        buildConfigField(STRING, "CONTACT_EMAIL", properties.getProperty("key.contact_email"))
        buildConfigField(STRING, "GOOGLE_MAP_ANDROID_API_KEY", properties.getProperty("google_map.android.api_key"))
        buildConfigField(STRING, "GOOGLE_MAP_IOS_API_KEY", properties.getProperty("google_map.ios.api_key"))
    }
}