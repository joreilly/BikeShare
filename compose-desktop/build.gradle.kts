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

// several Compose/lifecycle artifacts publish desktop jars with colliding file names,
// which the application plugin's flat lib/ distribution layout can't represent
tasks.named("distTar") { enabled = false }
tasks.named("distZip") { enabled = false }

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    //languageSettings.enableLanguageFeature("ExplicitBackingFields")
}