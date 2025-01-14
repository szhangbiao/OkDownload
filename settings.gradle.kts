pluginManagement {
    repositories {
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

rootProject.name = "OkDownload"
include(":app")
include(":okhttp")
include(":okdownload")
include(":sqlite")
include(":filedownloader")

project(":okhttp").projectDir = File(settingsDir, "okdownload-connection-okhttp")
project(":sqlite").projectDir = File(settingsDir, "okdownload-breakpoint-sqlite")
project(":filedownloader").projectDir = File(settingsDir, "okdownload-filedownloader")
