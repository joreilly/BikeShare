import SwiftUI
import UIKit

// Colours matching the Compose BikeShareTheme (common/.../ui/theme/BikeShareTheme.kt), with a
// separate value for light and dark mode.

private func rgb(_ hex: UInt32) -> UIColor {
    UIColor(red: CGFloat((hex >> 16) & 0xFF) / 255.0,
            green: CGFloat((hex >> 8) & 0xFF) / 255.0,
            blue: CGFloat(hex & 0xFF) / 255.0,
            alpha: 1.0)
}

private func adaptive(light: UInt32, dark: UInt32) -> Color {
    Color(UIColor { traits in traits.userInterfaceStyle == .dark ? rgb(dark) : rgb(light) })
}

// primary / secondary
let BikeGreen = adaptive(light: 0x007C30, dark: 0x73DC82)
let BikeBlue = adaptive(light: 0x005FAF, dark: 0xA5C8FF)

// Station availability: progress bar, bike icon and map pins. These differ in lightness as well
// as hue so they stay distinguishable with red-green colour blindness; counts use the normal text
// colour rather than these.
let lowAvailabilityColor = adaptive(light: 0x9B1B12, dark: 0xF28B73)
let mediumAvailabilityColor = adaptive(light: 0xFFA000, dark: 0xFFD43B)
let highAvailabilityColor = adaptive(light: 0x10A535, dark: 0x168C25)
