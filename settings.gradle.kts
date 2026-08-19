// Renaming a generated product happens here and in app/build.gradle.kts's namespace and
// applicationId. Those three lines are the whole rename — Gradle's Kotlin DSL is text, so
// there is no generated project file to keep in sync.
rootProject.name = "ForgeKit"

pluginManagement {
    repositories {
        google()
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

include(":app")
