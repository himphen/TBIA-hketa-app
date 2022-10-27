plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("dev.icerock.mobile.multiplatform-resources")
}

val coroutinesVersion = "1.6.4"
val ktorVersion = "2.1.2"
val sqlDelightVersion = "1.5.3"

kotlin {
    android()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Niseko"
        homepage = "https://github.com/himphen"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-client-resources:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.2.3")

                implementation("dev.gitlive:firebase-auth:1.6.2")
                implementation("dev.gitlive:firebase-database:1.6.2")
                implementation("dev.gitlive:firebase-config:1.6.2")

                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                implementation("dev.icerock.moko:resources:0.20.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:30.5.0"))
                implementation("com.google.firebase:firebase-analytics-ktx")
                implementation("com.google.firebase:firebase-crashlytics-ktx")
                implementation("com.google.firebase:firebase-storage-ktx")

                implementation("com.github.himphen:logger:3.0.1")

                implementation("com.squareup.sqldelight:android-driver:1.5.3")
            }
        }
        val androidTest by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")

                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "hibernate.v2.api"
    compileSdk = 31
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
}

sqldelight {
    database("KmbDatabase") {
        packageName = "hibernate.v2.database"
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "hibernate.v2.res" // required
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
    multiplatformResourcesVisibility = dev.icerock.gradle.MRVisibility.Internal // optional, default Public
    iosBaseLocalizationRegion = "en" // optional, default "en"
    multiplatformResourcesSourceSet = "commonMain"  // optional, default "commonMain"
}