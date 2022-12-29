//
//  KMMViewModel.swift
//  KMMViewModelCore
//
//  Created by Rick Clephas on 28/11/2022.
//

import Combine
import KMMViewModelCoreObjC

/// A Kotlin Multiplatform Mobile ViewModel.
public protocol KMMViewModel: ObservableObject where ObjectWillChangePublisher == ObservableObjectPublisher {
    /// The `ViewModelScope` of this `KMMViewModel`.
    var viewModelScope: ViewModelScope { get }
}
