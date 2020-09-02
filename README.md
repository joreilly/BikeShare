# BikeShare

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

Running on
* iOS
* macOS
* Android
* Web

Using following dependencies
* Kotlin: 1.4.0
* Kotlinx Coroutines: 1.3.9-native-mt
* Kotlinx Serialization: 1.0.0-RC
* Ktor: 1.4.0

Note that, due to use of Jetpack Compose, Android Studio Canary is required to build/run Android client (currently 4.2 Canary 7).
The iOS client has been tested in latest released version of XCode (currently 11.6). See below regarding macOS build.
When opening iOS and macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one)


### macOS client
The macOS client uses functionality only available in SwiftUI 2 and as a result requires use of XCode 12 beta. 


### Web client

There's a very basic Kotlin/JS (+react) client in `web` module.  To exercise this run `./gradlew :web:browserDevelopmentRun`.