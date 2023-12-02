# BikeShare

![kotlin-version](https://img.shields.io/badge/kotlin-1.9.20-blue)

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


<img width="668" alt="Screenshot 2022-12-24 at 15 16 22" src="https://user-images.githubusercontent.com/6302/209442335-862ecb89-8b22-454c-b7a8-d10dcd4deeda.png">



![BikeShare Screenshot](/art/screenshot_ios_swiftui.png?raw=true )


<img width="1382" alt="Screenshot 2023-12-02 at 14 00 11" src="https://github.com/joreilly/BikeShare/assets/6302/7c52f1a7-043f-47c3-b7e7-c57a25391e5b">

<img width="1293" alt="Screenshot 2023-12-02 at 14 10 34" src="https://github.com/joreilly/BikeShare/assets/6302/db821735-a8eb-4865-8723-4a6bb2de1f9f">



### Building

Need to use at least Android Studio Arctic Fox to build/run the Android client. 

Requires Xcode 13.2 or later (due to use of new Swift 5.5 concurrency APIs). When opening iOS project remember to open `.xcworkspace` file (and not `.xcodeproj` one)

### Compose for Desktop client

This client is available in `compose-desktop` module. Note that you need to use appropriate version of JVM when running (works for example with Java 11)

Note that this makes use of https://github.com/Shusshu/android-flags for flag images shown.
