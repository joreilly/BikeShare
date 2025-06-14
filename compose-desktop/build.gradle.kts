plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    //application
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.components.resources)
    implementation(libs.maplibre.compose)

    implementation(projects.common)
}

//application {
//    mainClass.set("MainKt")
//}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
    //languageSettings.enableLanguageFeature("ExplicitBackingFields")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs(
            "--add-opens",
            "java.desktop/java.awt.peer=ALL-UNNAMED"
        ) // recommended but not necessary

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}