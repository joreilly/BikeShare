// transitioning to version catalogs....

object Versions {
    const val compose = "1.5.4"
    const val composeCompiler = "1.5.4-dev-k1.9.20-50f08dfa4b4"
    const val jbComposeCompiler = "1.5.3"
    const val navCompose = "2.7.5"
    const val composeMaterial3 = "1.1.2"
    const val glance = "1.0.0"
    const val androidxLifecycle = "2.5.1"
}

object AndroidSdk {
    const val min = 21
    const val compile = 34
    const val target = compile
}

object Deps {
    const val androidXLifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}}"

    object Compose {
        const val compiler = "androidx.compose.compiler:compiler:${Versions.composeCompiler}"
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.navCompose}"

        const val material3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
        const val material3WindowSizeClass = "androidx.compose.material3:material3-window-size-class:${Versions.composeMaterial3}"

        const val glance = "androidx.glance:glance-appwidget:${Versions.glance}"
    }

}
