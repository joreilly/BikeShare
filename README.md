# BikeShare

![kotlin-version](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fjoreilly%2FBikeShare%2Fmain%2Fgradle%2Flibs.versions.toml&query=%24.versions.kotlin&label=kotlin&logo=kotlin&color=blue)

Compose Multiplatform and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).  Uses [Circuit](https://github.com/slackhq/circuit) and [kotlin-inject](https://github.com/evant/kotlin-inject).

Running on
* iOS (SwiftUI)
* Android (Jetpack Compose)
* Desktop (Compose for Desktop)
* Web (Wasm based Compose for Web)
* Swift Executable Package

Related posts:
* [Using SwiftUI and Compose to develop App Widgets on iOS and Android](https://johnoreilly.dev/posts/ios-android-widget-kmp/)
* [Bridging the gap between Swift 5.5 concurrency and Kotlin Coroutines with KMP-NativeCoroutines](https://johnoreilly.dev/posts/kmp-native-coroutines/)
* [Creating a Swift command line app that consumes Kotlin Multiplatform code - Part 2](https://johnoreilly.dev/posts/swift-command-line-kotlin-multiplatform-part2/)
* [Using kotlin-inject in a Kotlin/Compose Multiplatform project](https://johnoreilly.dev/posts/kotlin-inject-kmp/)

## Building and running

Requires JDK 17+ and a recent Android Studio release with AGP 9.x support (the project uses the new `com.android.kotlin.multiplatform.library` plugin).

* Android: `./gradlew :androidApp:installDebug` (or run the `androidApp` configuration in Android Studio)
* Desktop: `./gradlew :compose-desktop:run`
* Web: `./gradlew :compose-web:wasmJsBrowserDevelopmentRun`
* iOS: open `ios/BikeShare/BikeShare.xcodeproj` in Xcode and run

![BikeShare iOS (SwiftUI)](/art/screenshot_ios_swiftui.png?raw=true )

<img width="1132" height="819" alt="BikeShare running on desktop (Compose for Desktop)" src="https://github.com/user-attachments/assets/33d28fbf-112b-4ea9-8d51-5a65d1386a6c" />

<img width="1426" alt="BikeShare running in the browser (Wasm based Compose for Web)" src="https://github.com/joreilly/BikeShare/assets/6302/3e3092cd-261f-49a9-a0b3-bb6631a9d119">

Note that this makes use of https://github.com/Shusshu/android-flags for flag images shown.


## Full set of Kotlin Multiplatform/Compose/SwiftUI samples

*  PeopleInSpace (https://github.com/joreilly/PeopleInSpace)
*  GalwayBus (https://github.com/joreilly/GalwayBus)
*  Confetti (https://github.com/joreilly/Confetti)
*  BikeShare (https://github.com/joreilly/BikeShare)
*  FantasyPremierLeague (https://github.com/joreilly/FantasyPremierLeague)
*  ClimateTrace (https://github.com/joreilly/ClimateTraceKMP)
*  GeminiKMP (https://github.com/joreilly/GeminiKMP)
*  MortyComposeKMM (https://github.com/joreilly/MortyComposeKMM)
*  StarWars (https://github.com/joreilly/StarWars)
*  WordMasterKMP (https://github.com/joreilly/WordMasterKMP)
*  Chip-8 (https://github.com/joreilly/chip-8)
