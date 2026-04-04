# Bandung Air Quality

Bandung Air Quality is a simple Android application that retrieves and displays real-time air quality information for the Bandung area. The app fetches data from an external API and presents it in a clean and easy-to-read interface.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ba2299fa-5eef-48f1-bc41-9227a03e661f" width="380" />
  <img src="https://github.com/user-attachments/assets/a9800f53-e9d2-4909-8264-4ac044de6314" width="380" />
  <img src="https://github.com/user-attachments/assets/33fbadc5-4ffd-4e3a-be13-fc4ab824e518" width="380" />
  <img src="https://github.com/user-attachments/assets/9ba2019a-92c8-465d-907c-96de8c7677c5" width="380"/>


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
