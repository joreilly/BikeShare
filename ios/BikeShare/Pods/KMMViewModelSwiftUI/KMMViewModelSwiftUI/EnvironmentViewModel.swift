//
//  EnvironmentViewModel.swift
//  KMMViewModelSwiftUI
//
//  Created by Rick Clephas on 27/11/2022.
//

import SwiftUI
import KMMViewModelCore
import KMMViewModelCoreObjC

/// An `EnvironmentObject` property wrapper for `KMMViewModel`s.
@propertyWrapper
public struct EnvironmentViewModel<ViewModel: KMMViewModel>: DynamicProperty {
    
    @EnvironmentObject private var observableObject: ObservableViewModel<ViewModel>
    
    /// The underlying `KMMViewModel` referenced by the `EnvironmentViewModel`.
    public var wrappedValue: ViewModel { observableObject.viewModel }
    
    /// A projection of the observed `KMMViewModel` that creates bindings to its properties using dynamic member lookup.
    public var projectedValue: ObservableViewModel<ViewModel>.Projection {
        ObservableViewModel.Projection(observableObject)
    }
    
    /// Creates an `EnvironmentViewModel`.
    public init() { }
}

public extension View {
    /// Supplies a `KMMViewModel` to a view subhierarchy.
    /// - Parameter viewModel: The `KMMViewModel` to supply to a view subhierarchy.
    func environmentViewModel<ViewModel: KMMViewModel>(_ viewModel: ViewModel) -> some View {
        environmentObject(createObservableViewModel(for: viewModel))
    }
    
    /// Supplies a `KMMViewModel` to a view subhierarchy.
    /// - Parameter projectedValue: The `projectedValue` from e.g. `StateViewModel`.
    func environmentViewModel<ViewModel: KMMViewModel>(_ projectedValue: ObservableViewModel<ViewModel>.Projection) -> some View {
        environmentObject(projectedValue.observableObject)
    }
}
