//
//  ObservableViewModel.swift
//  KMMViewModelCore
//
//  Created by Rick Clephas on 27/11/2022.
//

import Combine
import KMMViewModelCoreObjC

/// Creates an `ObservableObject` for the specified `KMMViewModel`.
/// - Parameter viewModel: The `KMMViewModel` to wrap in an `ObservableObject`.
public func createObservableViewModel<ViewModel: KMMViewModel>(
    for viewModel: ViewModel
) -> ObservableViewModel<ViewModel> {
    ObservableViewModel(viewModel)
}

/// An `ObservableObject` for a `KMMViewModel`.
public final class ObservableViewModel<ViewModel: KMMViewModel>: ObservableObject {
    
    public let objectWillChange: ObservableViewModelPublisher
    
    private var _viewModel: ViewModel
    public var viewModel: ViewModel { _viewModel }
    
    internal init(_ viewModel: ViewModel) {
        objectWillChange = ObservableViewModelPublisher(viewModel.viewModelScope, viewModel.objectWillChange)
        self._viewModel = viewModel
    }
    
    public func get<T>(_ keyPath: KeyPath<ViewModel, T>) -> T {
        _viewModel[keyPath: keyPath]
    }
    
    public func set<T>(_ keyPath: WritableKeyPath<ViewModel, T>, to newValue: T) {
        _viewModel[keyPath: keyPath] = newValue
    }
}

/// Publisher for `ObservableViewModel` that connects to the `ViewModelScope`.
public final class ObservableViewModelPublisher: Publisher {
    public typealias Output = Void
    public typealias Failure = Never
    
    internal weak var viewModelScope: ViewModelScope?
    
    private let publisher = ObservableObjectPublisher()
    private var objectWillChangeCancellable: AnyCancellable? = nil
    
    init(_ viewModelScope: ViewModelScope, _ objectWillChange: ObservableObjectPublisher) {
        self.viewModelScope = viewModelScope
        viewModelScope.setSendObjectWillChange { [weak self] in
            self?.publisher.send()
        }
        objectWillChangeCancellable = objectWillChange.sink { [weak self] _ in
            self?.publisher.send()
        }
    }
    
    public func receive<S>(subscriber: S) where S : Subscriber, Never == S.Failure, Void == S.Input {
        viewModelScope?.increaseSubscriptionCount()
        publisher.receive(subscriber: ObservableViewModelSubscriber(self, subscriber))
    }
    
    deinit {
        viewModelScope?.cancel()
    }
}

/// Subscriber for `ObservableViewModelPublisher` that creates `ObservableViewModelSubscription`s.
private class ObservableViewModelSubscriber<S>: Subscriber where S : Subscriber, Never == S.Failure, Void == S.Input {
    typealias Input = Void
    typealias Failure = Never
    
    private let publisher: ObservableViewModelPublisher
    private let subscriber: S
    
    init(_ publisher: ObservableViewModelPublisher, _ subscriber: S) {
        self.publisher = publisher
        self.subscriber = subscriber
    }
    
    func receive(subscription: Subscription) {
        subscriber.receive(subscription: ObservableViewModelSubscription(publisher, subscription))
    }
    
    func receive(_ input: Void) -> Subscribers.Demand {
        subscriber.receive(input)
    }
    
    func receive(completion: Subscribers.Completion<Never>) {
        subscriber.receive(completion: completion)
    }
}

/// Subscription for `ObservableViewModelPublisher` that decreases the subscription count upon cancellation.
private class ObservableViewModelSubscription: Subscription {
    
    private let publisher: ObservableViewModelPublisher
    private let subscription: Subscription
    
    init(_ publisher: ObservableViewModelPublisher, _ subscription: Subscription) {
        self.publisher = publisher
        self.subscription = subscription
    }
    
    func request(_ demand: Subscribers.Demand) {
        subscription.request(demand)
    }
    
    private var cancelled = false
    
    func cancel() {
        subscription.cancel()
        guard !cancelled else { return }
        cancelled = true
        publisher.viewModelScope?.decreaseSubscriptionCount()
    }
}
