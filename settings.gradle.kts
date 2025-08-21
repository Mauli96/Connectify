pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("androidx.*")
                includeGroup("com.google.firebase")   // ✅ Firebase artifacts
                includeGroup("com.google.gms")       // ✅ Google Services plugin/artifacts
                includeGroupByRegex("com\\.google.*")// (kept, but the two lines above make it explicit)
                includeGroup("org.jetbrains.kotlin") // Kotlin plugins
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("androidx.*")
                includeGroup("com.google.firebase")   // ✅ libraries resolve here
                includeGroup("com.google.gms")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Connectify"
include(":app")