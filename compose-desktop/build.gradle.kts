plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    application
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(projects.common)
}

application {
    mainClass.set("MainKt")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    //languageSettings.enableLanguageFeature("ExplicitBackingFields")
}