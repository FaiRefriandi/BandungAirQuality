# Bandung Air Quality

Bandung Air Quality is a simple Android application that retrieves and displays real-time air quality information for the Bandung area. The app fetches data from an external API and presents it in a clean and easy-to-read interface.

<p align="center">
  <img src="https://github.com/user-attachments/assets/1317efb7-ad83-4a5f-95e0-8c2d993b57d7" width="250" />
  <img src="https://github.com/user-attachments/assets/3a6f56a3-21fa-435d-9a5a-200a5865e86c" width="250" />
  <img src="https://github.com/user-attachments/assets/e06159b1-cece-4440-90b5-2c20d62eb38d" width="250" />
</p>

---

## Overview

This project is built using native Android development.  
Key functionalities include:
- Fetching real-time AQI (Air Quality Index)
- Displaying temperature, humidity, and pollution levels
- Simple and minimal UI
- Uses ViewModel and LiveData for state handling

---

## Features

- Real-time air quality data for Bandung
- Displays AQI, temperature, humidity, and other environmental values
- Automatically fetches data from the AirVisual API
- Error handling for failed requests
- Pull-to-refresh (if implemented)
- Lightweight and easy to extend

---

## Tech Stack

- Android (Kotlin)
- XML layout
- ViewModel / LiveData
- Retrofit or OkHttp for API calls (depending on your implementation)
- AirVisual API (or preferred AQI provider)

---

## Installation

1. Clone the repository
```bash
git clone https://github.com/FaiRefriandi/BandungAirQuality.git
```
3. Open the project in Android Studio.
4. Sync Gradle.
5. Insert your API key in the code (AirVisual or other provider).
6. Build and run the app on an emulator or Android device.

---

## Requirements

- Android Studio Hedgehog or newer
- Valid API key for air quality data
- Internet connection

---

## License

This project uses the MIT License.

MIT License © 2025 Fai Refriandi
