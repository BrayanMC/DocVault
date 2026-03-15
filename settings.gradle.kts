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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DocVault"

include(":app")

// Core
include(":core:core-common")
include(":core:core-ui")
include(":core:core-designsystem")
include(":core:core-navigation")

// Domain
include(":domain")

// Data
include(":data")

// Features
include(":features:feature_documents")
include(":features:feature_detail")

// Libraries
include(":libraries:lib-security")
include(":libraries:lib-camera")
include(":libraries:lib-location")
include(":test-utils")
