# KMM-ViewModel

A library that allows you to share ViewModels between Android and iOS.

## Compatibility

The latest version of the library uses Kotlin version `1.7.21`.  
Compatibility versions for newer Kotlin versions are also available:

| Version      | Version suffix    |   Kotlin   | Coroutines |
|--------------|-------------------|:----------:|:----------:|
| _latest_     | -kotlin-1.8.0-RC2 | 1.8.0-RC2  |   1.6.4    |
| **_latest_** | **_no suffix_**   | **1.7.21** | **1.6.4**  |

## Kotlin

Add the library to your shared Kotlin module:
```kotlin
dependencies {
    implementation("com.rickclephas.kmm:kmm-viewmodel-core:<version>")
}
```

Create your ViewModels almost as you would in Android:
```kotlin
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.stateIn

// 1: use KMMViewModel instead of ViewModel
open class TimeTravelViewModel: KMMViewModel() {

    private val clockTime = Clock.time

    /**
     * A [StateFlow] that emits the actual time.
     */
    val actualTime = clockTime.map { formatTime(it) }
        // 2: supply viewModelScope to your stateIn calls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "N/A")

    // 3: supply viewModelScope to your MutableStateFlows
    private val _travelEffect = MutableStateFlow<TravelEffect?>(viewModelScope, null)
    /**
     * A [StateFlow] that emits the applied [TravelEffect].
     */
    val travelEffect = _travelEffect.asStateFlow()
}
```

You need to use `viewModelScope` wherever possible to propagate state changes to iOS.  
In other cases you can access the `ViewModelScope.coroutineScope` property directly.

### KMP-NativeCoroutines

Use the `@NativeCoroutinesState` annotation from [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines)
to turn your `StateFlow`s into properties in Swift:

```kotlin
@NativeCoroutinesState
val travelEffect = _travelEffect.asStateFlow()
```

Checkout the [README](https://github.com/rickclephas/KMP-NativeCoroutines/blob/dev-1.0/README.md)
for more information and installation instructions for KMP-NativeCoroutines.

<details><summary>Alternative</summary>
<p>

Alternatively you can create extension properties in your iOS source-set yourself:
```kotlin
val TimeTravelViewModel.travelEffectValue: TravelEffect?
    get() = travelEffect.value
```
</p>
</details>

## Android

Add the library to your Android module:
```kotlin
dependencies {
    implementation("com.rickclephas.kmm:kmm-viewmodel-core:<version>")
}
```

Use the view model like you would any other Android view model:
```kotlin
class TimeTravelFragment: Fragment(R.layout.fragment_time_travel) {
    private val viewModel: TimeTravelViewModel by viewModels()
}
```

> **Note:** support for Jetpack Compose is coming soon.

## Swift

Add the Swift package to your `Package.swift` file:
```swift
dependencies: [
    .package(url: "https://github.com/rickclephas/KMM-ViewModel.git", from: "<version>")
]
```

Or add it in Xcode by going to `File` > `Add Packages...` and providing the URL:
`https://github.com/rickclephas/KMM-ViewModel.git`.

Create a `KMMViewModel.swift` file with the following contents:
```swift
import KMMViewModelCore
import shared // This should be your shared KMM module

extension Kmm_viewmodel_coreKMMViewModel: KMMViewModel { }
```

After that you can use your view model almost as if it were an `ObservableObject`.   
Just use the view model specific property wrappers and functions:

| `ObservableObject`      | `KMMViewModel`             |
|-------------------------|----------------------------|
| `@StateObject`          | `@StateViewModel`          |
| `@ObservedObject`       | `@ObservedViewModel`       |
| `@EnvironmentObject`    | `@EnvironmentViewModel`    |
| `environmentObject(_:)` | `environmentViewModel(_:)` |

E.g. to use the `TimeTravelViewModel` as a `StateObject`:
```swift
import SwiftUI
import KMMViewModelSwiftUI
import shared // This should be your shared KMM module

struct ContentView: View {
    @StateViewModel var viewModel = TimeTravelViewModel()
}
```

It's also possible to subclass your view model in Swift:
```swift
import Combine
import shared // This should be your shared KMM module

class TimeTravelViewModel: shared.TimeTravelViewModel {
    @Published var isResetDisabled: Bool = false
}
```
