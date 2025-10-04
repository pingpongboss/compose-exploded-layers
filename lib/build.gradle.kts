import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("maven-publish")
}

android {
    namespace = "com.github.pingpongboss.explodedlayers"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        lint.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin { compilerOptions { jvmTarget = JvmTarget.fromTarget("11") } }
    buildFeatures { compose = true }

    publishing { singleVariant("release") {} }
}

version =
    runCatching {
            // Try to get the latest annotated or lightweight tag name
            "git describe --tags --abbrev=0".runCommand().trim()
        }
        .getOrElse {
            "0.0.0-SNAPSHOT" // fallback if no tags yet
        }

fun String.runCommand(): String =
    ProcessBuilder(*split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()

publishing {
    publications {
        create<MavenPublication>("release") {
            // For Android:
            afterEvaluate { from(components["release"]) }

            groupId = "com.github.pingpongboss"
            artifactId = "compose-exploded-layers"

            pom {
                name.set("Exploded Layers for Jetpack Compose")
                description.set("Turn any composable into an interactive “3D exploded view”.")
                url.set("https://github.com/pingpongboss/compose-exploded-layers")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("com.github.pingpongboss")
                        name.set("Mark Wei")
                        email.set("markwei@gmail.com")
                    }
                }
                scm {
                    connection.set(
                        "scm:git:git://github.com/pingpongboss/compose-exploded-layers.git"
                    )
                    developerConnection.set(
                        "scm:git:ssh://github.com/pingpongboss/compose-exploded-layers.git"
                    )
                    url.set("https://github.com/pingpongboss/compose-exploded-layers")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
