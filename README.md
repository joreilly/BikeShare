# BikeShare

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

Running on
* iOS
* macOS
* Android

![BikeShare Screenshot](/art/screenshot1.png?raw=true )
![BikeShare Screenshot](/art/screenshot2.png?raw=true )

Note that, due to use of Jetpack Compose, Android Studio Canary is required to build/run Android client (currently 4.2 Canary 7).
The iOS client has been tested in latest released version of XCode (currently 12.0). See below regarding macOS build.
When opening iOS and macOS projects remember to open `.xcworkspace` file (and not `.xcodeproj` one)


### macOS client
The macOS client uses functionality only available in SwiftUI 2 and as a result requires use of XCode 12.2 beta.


Note that this makes use of https://github.com/Shusshu/android-flags for flag images shown.