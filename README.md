# BikeShare

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

Running on
* iOS (SwiftUI)
* macOS (SwiftUI)
* Android (Jetpack Compose)
* Desktop (Compose for Desktop)

![BikeShare Screenshot](/art/screenshot1.png?raw=true )
![BikeShare Screenshot](/art/screenshot2.png?raw=true )

Note that, due to use of Jetpack Compose, Android Studio Arctic Fox is required to build/run the Android client. The iOS and macOS clients have been tested in latest released version of XCode (currently 12.4).
When opening iOS and macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one)

### Compose for Desktop client

This client is available in `compose-desktop` module.  Note that you currently need to use EAP version of kotlin
plugin and also use appropriate JVM when running (works for example with Java 11)


Note that this makes use of https://github.com/Shusshu/android-flags for flag images shown.