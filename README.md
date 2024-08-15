# BikeShare

![kotlin-version](https://img.shields.io/badge/kotlin-2.0.0-blue?logo=kotlin)

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

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



<img width="668" alt="Screenshot 2022-12-24 at 15 16 22" src="https://user-images.githubusercontent.com/6302/209442335-862ecb89-8b22-454c-b7a8-d10dcd4deeda.png">

![BikeShare Screenshot](/art/screenshot_ios_swiftui.png?raw=true )

<img width="1426" alt="Screenshot 2023-12-02 at 15 01 25" src="https://github.com/joreilly/BikeShare/assets/6302/3e3092cd-261f-49a9-a0b3-bb6631a9d119">

<img width="1293" alt="Screenshot 2023-12-02 at 14 10 34" src="https://github.com/joreilly/BikeShare/assets/6302/db821735-a8eb-4865-8723-4a6bb2de1f9f">



### Building

Need to use at least Android Studio Arctic Fox to build/run the Android client. 

Requires Xcode 13.2 or later (due to use of new Swift 5.5 concurrency APIs). When opening iOS project remember to open `.xcworkspace` file (and not `.xcodeproj` one)

### Compose for Desktop client

This client is available in `compose-desktop` module. Note that you need to use appropriate version of JVM when running (works for example with Java 11)

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
