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

tasks.named<Sync>("installJvmDist") { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }

tasks.withType<Copy>().configureEach { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }

tasks.register<Jar>("jvmDesktopDistribution") {
    group = "distribution"
    description = "Assembles a runnable single-JAR distribution for the JVM desktop sample."

    archiveBaseName.set("ExplodedLayersDesktop")
    archiveVersion.set("")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))

    manifest {
        attributes["Main-Class"] = "io.github.pingpongboss.explodedlayers.samples.jvm.MainKt"
    }

    from({
        configurations.getByName("jvmRuntimeClasspath").map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
    from(layout.buildDirectory.dir("classes/kotlin/jvm/main"))

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
