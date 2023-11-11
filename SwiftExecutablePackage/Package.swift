// swift-tools-version:5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "BikeShare",
    platforms: [.macOS(.v13)],
    dependencies: [
        .package(url: "https://github.com/rickclephas/KMP-NativeCoroutines.git", exact: "1.0.0-ALPHA-18"),
        .package(url: "https://github.com/joreilly/BikeShareSwiftPackage", exact: "1.20.0"),
        .package(url: "https://github.com/apple/swift-argument-parser.git", exact: "1.2.0"),
        .package(url: "https://github.com/pakLebah/ANSITerminal", from: "0.0.3")
    ],
    targets: [
        .executableTarget(
            name: "bikeshare",
            dependencies: [
                .product(name: "KMPNativeCoroutinesAsync", package: "KMP-NativeCoroutines"),
                .product(name: "BikeShareKit", package: "BikeShareSwiftPackage"),
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
                .product(name: "ANSITerminal", package: "ANSITerminal")
            ]
        )
    ]
)
