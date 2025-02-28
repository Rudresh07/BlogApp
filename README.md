# Android Blog App

## Overview

The Android Blog App is a feature-rich application that allows users to browse blogs online, view saved offline blogs, and navigate through blog details seamlessly. It supports pagination for online content and provides a smooth user experience.

## Features

- **Fetch Online Blogs**: Retrieves the latest blogs from the server.
- **Offline Mode**: Saves blogs for offline reading.
- **Pagination Support**: Users can load the next set of blogs.
- **Blog Details View**: Read full blog content.
- **Material Design UI**: Uses Jetpack Compose for a modern UI.
- **Internet Connectivity Check**: Automatically detects online/offline status.

## Installation

### Prerequisites

- Android Studio (latest version recommended)
- Kotlin as the primary development language
- Gradle (configured in Android Studio)

### Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-repo/android-blog-app.git
   cd android-blog-app

  ##  Usage Guide

🔹 1. Launching the App

Upon opening, the app detects the internet connection status:

🌐 Online Mode: Fetches and displays blogs from the server.

📴 Offline Mode: Loads saved blogs from local storage.

🔹 2. Viewing Blogs

📜 Scroll through the list of available blogs.

👆 Tap on a blog to open its details.

🔹 3. Loading More Blogs

➡️ Click on the Next button at the bottom of the list.

🔄 New blogs will be fetched and added to the list.

🔹 4. Offline Blog Access

🚫 If there is no internet connection, the app displays saved blogs.

⚠️ If no blogs are saved, a message appears: "Nothing to show offline. Please connect to the internet."

🔹 5. Navigating to Blog Details

🔍 Clicking a blog navigates to the Blog Detail Screen.

📂 Offline blogs open a different detail screen that loads from local storage.

## Technology Stack

💻 Language: Kotlin

🏛️ Architecture: MVVM

🎨 UI Framework: Jetpack Compose

🌐 Networking: Retrofit

🗄️ Local Storage: Room Database

## Troubleshooting

❌ App is not fetching blogs

✅ Ensure the device is connected to the internet.

🔄 Restart the app.

❌ Offline blogs are not loading

📂 Check if blogs were saved previously.

🗑️ Clear cache and reload.

📢 Conclusion

The Android Blog App provides an intuitive way to read blogs online and offline with a seamless user experience.
