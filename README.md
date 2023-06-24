# BikeShare

![kotlin-version1](https://img.shields.io/badge/kotlin-1.9.0--RC-blue)

Jetpack Compose and SwiftUI based Kotlin Multiplatform sample project (based on [CityBikes API](http://api.citybik.es/v2/)).

Running on
* iOS (SwiftUI)
* Android (Jetpack Compose)
* Desktop (Compose for Desktop)

Related posts:
* [Using SwiftUI and Compose to develop App Widgets on iOS and Android](https://johnoreilly.dev/posts/ios-android-widget-kmp/)
* [Bridging the gap between Swift 5.5 concurrency and Kotlin Coroutines with KMP-NativeCoroutines](https://johnoreilly.dev/posts/kmp-native-coroutines/)


<img width="668" alt="Screenshot 2022-12-24 at 15 16 22" src="https://user-images.githubusercontent.com/6302/209442335-862ecb89-8b22-454c-b7a8-d10dcd4deeda.png">



![BikeShare Screenshot](/art/screenshot_ios_swiftui.png?raw=true )


### Building

Need to use at least Android Studio Arctic Fox to build/run the Android client. 

Requires XCode 13.2 or later (due to use of new Swift 5.5 concurrency APIs). When opening iOS project remember to open `.xcworkspace` file (and not `.xcodeproj` one)

### Compose for Desktop client

This client is available in `compose-desktop` module. Note that you need to use appropriate version of JVM when running (works for example with Java 11)

Note that this makes use of https://github.com/Shusshu/android-flags for flag images shown.
