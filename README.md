# NammaSkill Android App

A complete production-ready Android app to help rural youth in Karnataka discover and apply for government vocational skill training courses.

## Concept
NammaSkill bridges the gap between Skill Development Centers and unemployed youth in small towns by providing a one-tap application process for vocational courses.

## Tech Stack
- **Language:** Kotlin
- **Architecture:** MVVM + Clean Architecture
- **UI:** Material Design 3, ViewBinding, Navigation Component
- **Backend:** Firebase Firestore, Firebase Cloud Messaging
- **Maps:** Google Maps SDK
- **Utilities:** Glide (Image loading), Coroutines (Async), Lottie (Animations), Shimmer (Loading states)

## Setup Instructions

1. **Firebase Configuration:**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `com.nammaskill.app`.
   - Download `google-services.json` and place it in the `app/` directory of this project.
   - Enable **Cloud Firestore** and **Cloud Messaging** in the Firebase console.

2. **Google Maps API Key:**
   - Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/).
   - Enable **Maps SDK for Android**.
   - Open `app/src/main/res/values/strings.xml` and replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual key.

3. **Build & Run:**
   - Open the project in Android Studio.
   - Sync project with Gradle files.
   - Run on an emulator or physical device (Min SDK 24).

## Features
- **Splash & Onboarding:** Beautiful saffron-themed entry with district selection.
- **Course Finder:** Advanced filtering by trade, duration, and job guarantee.
- **Course Detail:** Comprehensive course info with trainer contact and seat tracking.
- **One-Tap Apply:** Simplified application form with live summary preview.
- **Center Map:** Visual discovery of skill centers across Karnataka.
- **Success Stories:** Real-world impact stories with animated statistics.
- **User Profile:** Manage applications and preferred trade notifications.

## Data Models
- `Course`: Course details, eligibility, and center info.
- `SkillCenter`: Geographic location and trades offered.
- `SuccessStory`: Before/after impact stories of graduates.
- `Application`: User application records.

## Project Structure
- `ui/`: Fragments and ViewModels organized by feature.
- `data/`: Models and `FirebaseRepository` for data management.
- `service/`: FCM messaging service for notifications.
- `util/`: Helpers for Preferences, Notifications, and Network status.
