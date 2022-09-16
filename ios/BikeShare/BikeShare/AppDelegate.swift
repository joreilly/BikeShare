//
//  AppDelegate.swift
//  BikeShare
//
//  Created by John O'Reilly on 30/07/2020.
//  Copyright © 2020 John O'Reilly. All rights reserved.
//

import UIKit
import ActivityKit
import common
import KMPNativeCoroutinesAsync
import BackgroundTasks
import os


private let logger = Logger(subsystem: Bundle.main.bundleIdentifier!, category: "AppDelegate")


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var backgroundTask: UIBackgroundTaskIdentifier = UIBackgroundTaskIdentifier.invalid



    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        KoinKt.doInitKoin()

        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            
            if let error = error {
                print(error)
            }
        }

        //UNUserNotificationCenter.current().delegate = self

        BackgroundTaskManager.shared.register()
        
        
        return true
    }
    
//    func registerBackgroundTask() {
//        backgroundTask = UIApplication.shared.beginBackgroundTask { [weak self] in
//            print("hi")
//
//            Task {
//                let repository = CityBikesRepository()
//
//                do {
//                    let stream = asyncStream(for: repository.pollNetworkUpdatesNative(network: "galway"))
//                    for try await data in stream {
//                        //self.stationList = data
//
//                        let station = data[0]
//                        print(station)
//                        //updateStationInfo(station: station)
//
//                    }
//                } catch {
//                    print("Failed with error: \(error)")
//                }
//
//            }
//
//            //self?.endBackgroundTask()
//        }
//        //assert(backgroundTask != UIBackgroundTaskInvalid)
//    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }

}





