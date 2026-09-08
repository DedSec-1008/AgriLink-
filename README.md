# AgriLink (Android)

An intelligent agricultural market-linkage & price discovery platform for farmers and FPOs, built natively for Android using Kotlin and Jetpack Compose.

## Core Features
- **Farmer Home Dashboard**: Summary of current produce, regional market price indicators, active transaction tracking card, and quick navigation.
- **Sell Produce Wizard**: Multi-step lot creation flow including commodity selection, quality grading, quantity & storage timing, net realization calculation, and buyer recommendation matching.
- **Regional Market Prices**: Real-time mandi price discovery with daily price trend changes and crop filtering.
- **My Lots & Competitive Offers**: Lot listings, status badges, incoming buyer offers with transparent net realization breakdown (deducting transport and handling costs).
- **End-to-End 6-Stage Transaction Lifecycle**:
  1. Offer Accepted & Contract Confirmation
  2. Logistics Booking (Vehicle selection, transporter rating, cost per quintal)
  3. Produce Pickup & Dispatch confirmation
  4. Delivery Tracking with stage milestones
  5. Payment Tracking & Delay Flagging
  6. Sale Completion & Buyer Rating
- **Verified Buyers Directory**: Verified buyer profiles, payment turnaround metrics, on-time payment records, and demand listings.
- **Help Desk & Grievance Support**: Category guides, 24x7 toll-free helpline, voice assistant entry point, and formal issue escalation dialog.
- **Multilingual Support**: Dynamic runtime localization in English, Hindi (हिन्दी), and Marathi (मराठी).
- **Adaptive Layouts**: Full support for portrait and landscape orientations (Navigation Rail).

## Tech Stack
- **Framework**: Android SDK 36, Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose, Material Design 3 (M3)
- **Architecture**: MVVM with Repository Pattern, StateFlow & Coroutines
- **Testing**: Local JVM tests using Robolectric and Roborazzi screenshot tests
- **Build System**: Gradle (Kotlin DSL)

