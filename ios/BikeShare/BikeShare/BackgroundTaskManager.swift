import Foundation
import UserNotifications
import BackgroundTasks
import os.log
import common
import KMPNativeCoroutinesAsync

private let logger = Logger(subsystem: Bundle.main.bundleIdentifier!, category: "BackgroundAppRefreshManager")
//private let backgroundTaskIdentifier = "com.robertmryan.BGAppRefresh.refresh"

private let backgroundTaskIdentifier = "dev.johnoreilly.BikeShare.refresh"

class BackgroundTaskManager {
    static let shared = BackgroundTaskManager()

    private init() { }
}

// MARK: Public methods

extension BackgroundTaskManager {

    func register() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: backgroundTaskIdentifier, using: .main, launchHandler: handleTask(_:))
    }
    
    func handleTask(_ task: BGTask) {
        print("handleTask")
        //scheduleAppRefresh()

        //show(message: task.identifier)

        let request = performRequest { error in
            task.setTaskCompleted(success: error == nil)
        }

        task.expirationHandler = {
            task.setTaskCompleted(success: false)
            request.cancel()
        }
    }

    func scheduleAppRefresh() {
        let request = BGProcessingTaskRequest(identifier: backgroundTaskIdentifier)
        
        request.earliestBeginDate = Date(timeIntervalSinceNow: 0)
        request.requiresNetworkConnectivity = true
        request.requiresExternalPower = false // Default value in false
        
        BGTaskScheduler.shared.cancelAllTaskRequests()
        
        
        var message = "Scheduled"
        do {
            try BGTaskScheduler.shared.submit(request)
            logger.log("task request submitted to scheduler")

            // at (lldb) prompt, type:
            //
            // e -l objc -- (void)[[BGTaskScheduler sharedScheduler] _simulateLaunchForTaskWithIdentifier:@"dev.johnoreilly.BikeShare.refresh"]
        } catch BGTaskScheduler.Error.notPermitted {
            message = "BGTaskScheduler.shared.submit notPermitted"
        } catch BGTaskScheduler.Error.tooManyPendingTaskRequests {
            message = "BGTaskScheduler.shared.submit tooManyPendingTaskRequests"
        } catch BGTaskScheduler.Error.unavailable {
            message = "BGTaskScheduler.shared.submit unavailable"
        } catch {
            message = "BGTaskScheduler.shared.submit \(error.localizedDescription)"
        }
        
        print(message)

        //show(message: message)

        
        BGTaskScheduler.shared.getPendingTaskRequests(completionHandler: { request in
                                    print("Pending task requests: \(request)")
                                })

        print(Date())
    }
}

// MARK: - Private utility methods

private extension BackgroundTaskManager {

//    func show(message: String) {
//        logger.debug("\(message, privacy: .public)")
//        let content = UNMutableNotificationContent()
//        content.title = "AppRefresh task"
//        content.body = message
//        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
//        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)
//        UNUserNotificationCenter.current().add(request) { error in
//            if let error = error {
//                logger.error("\(message, privacy: .public) error: \(error.localizedDescription, privacy: .public)")
//            }
//        }
//    }

    //@discardableResult
    func performRequest(completion: @escaping (Error?) -> Void) -> URLSessionTask {
        print("starting bg network request")

        
        
        Task {
            do {
                let repository = CityBikesRepository()
                let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: "galway"))
                for try await data in stream {
                    //self.stationList = data
                    
                    let station = data[0]
                    print(station)
                    //updateStationInfo(station: station)
                    
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }

        
        let url = URL(string: "https://httpbin.org/get")!
        let task = URLSession.shared.dataTask(with: url) { _, _, error in
            print("finished bg network request")
            completion(error)
        }

        task.resume()

        return task
    }
}
