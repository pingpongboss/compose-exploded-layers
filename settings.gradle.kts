pluginManagement {
    repositories {
        // wasm
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // disabled for wasm repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // wasm
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }
}

// package.json fails to generate due to illegal characters (spaces)
rootProject.name = "exploded.layers"

include(":lib")

include(":sample")
