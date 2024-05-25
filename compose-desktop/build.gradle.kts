plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    application
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jxmapviewer:jxmapviewer2:2.8")
    implementation(project(":common"))
}

application {
    mainClass.set("MainKt")
}
