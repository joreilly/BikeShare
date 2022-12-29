//
//  ObservableViewModelProjection.swift
//  KMMViewModelSwiftUI
//
//  Created by Rick Clephas on 27/11/2022.
//

import SwiftUI
import KMMViewModelCore

public extension ObservableViewModel {
    
    /// A projection of a `KMMViewModel` that creates bindings to its properties using dynamic member lookup.
    @dynamicMemberLookup
    struct Projection {
        
        internal let observableObject: ObservableViewModel<ViewModel>
        
        internal init(_ observableObject: ObservableViewModel<ViewModel>) {
            self.observableObject = observableObject
        }
        
        public subscript<T>(dynamicMember keyPath: WritableKeyPath<ViewModel, T>) -> Binding<T> {
            Binding {
                observableObject.get(keyPath)
            } set: { value in
                observableObject.set(keyPath, to: value)
            }
        }
    }
}
