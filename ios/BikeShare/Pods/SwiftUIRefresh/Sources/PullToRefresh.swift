import SwiftUI
import Introspect

private struct PullToRefresh: UIViewRepresentable {
    
    @Binding var isShowing: Bool
    let onRefresh: () -> Void
    
    public init(
        isShowing: Binding<Bool>,
        onRefresh: @escaping () -> Void
    ) {
        _isShowing = isShowing
        self.onRefresh = onRefresh
    }
    
    public class Coordinator {
        let onRefresh: () -> Void
        let isShowing: Binding<Bool>
        
        init(
            onRefresh: @escaping () -> Void,
            isShowing: Binding<Bool>
        ) {
            self.onRefresh = onRefresh
            self.isShowing = isShowing
        }
        
        @objc
        func onValueChanged() {
            isShowing.wrappedValue = true
            onRefresh()
        }
    }
    
    public func makeUIView(context: UIViewRepresentableContext<PullToRefresh>) -> UIView {
        let view = UIView(frame: .zero)
        view.isHidden = true
        view.isUserInteractionEnabled = false
        return view
    }
    
    private func tableView(entry: UIView) -> UITableView? {
        
        // Search in ancestors
        if let tableView = Introspect.findAncestor(ofType: UITableView.self, from: entry) {
            return tableView
        }

        guard let viewHost = Introspect.findViewHost(from: entry) else {
            return nil
        }

        // Search in siblings
        return Introspect.previousSibling(containing: UITableView.self, from: viewHost)
    }

    public func updateUIView(_ uiView: UIView, context: UIViewRepresentableContext<PullToRefresh>) {
        
        DispatchQueue.main.asyncAfter(deadline: .now()) {
            
            guard let tableView = self.tableView(entry: uiView) else {
                return
            }
            
            if let refreshControl = tableView.refreshControl {
                if self.isShowing {
                    refreshControl.beginRefreshing()
                } else {
                    refreshControl.endRefreshing()
                }
                return
            }
            
            let refreshControl = UIRefreshControl()
            refreshControl.addTarget(context.coordinator, action: #selector(Coordinator.onValueChanged), for: .valueChanged)
            tableView.refreshControl = refreshControl
        }
    }
    
    public func makeCoordinator() -> Coordinator {
        return Coordinator(onRefresh: onRefresh, isShowing: $isShowing)
    }
}

extension View {
    public func pullToRefresh(isShowing: Binding<Bool>, onRefresh: @escaping () -> Void) -> some View {
        return overlay(
            PullToRefresh(isShowing: isShowing, onRefresh: onRefresh)
                .frame(width: 0, height: 0)
        )
    }
}
