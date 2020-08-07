# BikeShare

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

Currently using Kotlin 1.4.0-rc and running on:
* iOS
* macOS
* Android
* Web

Based at the moment on following dependencies:
* Kotlin: 1.4.0-rc
* Kotlinx Coroutines: 1.3.8-native-mt-1.4.0-rc
* Kotlinx Serialization: 1.0-M1-1.4.0-rc
* Ktor: 1.3.2-1.4.0-rc

Note that, due to use of Jetpack Compose, Android Studio Canary is required to build/run Android client (currently 4.2 Canary 7).
The iOS client has been tested in latest released version of XCode (currently 11.6). See below regarding macOS build.
When opening iOS and macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one)


### macOS client
The macOS client uses functionality only available in SwiftUI 2 and as a result requires use of XCode 12 beta.  Also
important to note that it does not work right now with `native-mt` version of Kotlinx Coroutines (hopefully this will change soon)
so that dependency (in `Dependencies.kt`) needs to be updated for that to run).


### Web client

There's a very basic Kotlin/JS (+react) client in `web` module.  To exercise this run `./gradlew :web:browserDevelopmentRun`.