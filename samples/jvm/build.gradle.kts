import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            // Configures a JavaExec task named "runJvm" and a Gradle distribution for the "main"
            // compilation in this target
            executable { mainClass.set("io.github.pingpongboss.explodedlayers.samples.jvm.MainKt") }
        }
    }

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":samples:common"))
            implementation(project(":lib"))

            implementation(compose.desktop.currentOs)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.animation)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.pingpongboss.explodedlayers.samples.jvm.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Exe, TargetFormat.Deb)
        }
    }
}
