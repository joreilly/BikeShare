//
//  StateViewModel.swift
//  KMMViewModelSwiftUI
//
//  Created by Rick Clephas on 27/11/2022.
//

import SwiftUI
import KMMViewModelCore
import KMMViewModelCoreObjC

/// A `StateObject` property wrapper for `KMMViewModel`s.
@available(iOS 14.0, macOS 11.0, tvOS 14.0, watchOS 7.0, *)
@propertyWrapper
public struct StateViewModel<ViewModel: KMMViewModel>: DynamicProperty {
    
    @StateObject private var observableObject: ObservableViewModel<ViewModel>
    
    /// The underlying `KMMViewModel` referenced by the `StateViewModel`.
    public var wrappedValue: ViewModel { observableObject.viewModel }
    
    /// A projection of the observed `KMMViewModel` that creates bindings to its properties using dynamic member lookup.
    public var projectedValue: ObservableViewModel<ViewModel>.Projection {
        ObservableViewModel.Projection(observableObject)
    }
    
    /// Creates a `StateViewModel` for the specified `KMMViewModel`.
    /// - Parameter wrappedValue: The `KMMViewModel` to observe.
    public init(wrappedValue: @autoclosure @escaping () -> ViewModel) {
        self._observableObject = StateObject(wrappedValue: createObservableViewModel(for: wrappedValue()))
    }
}
