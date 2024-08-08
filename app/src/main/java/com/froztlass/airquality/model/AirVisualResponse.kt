package com.froztlass.airquality.model

data class AirVisualResponse(
    val status: String,
    val data: Data?
)

data class Data(
    val city: String,
    val state: String,
    val country: String,
    val location: Location,
    val current: Current
)

data class Location(
    val type: String,
    val coordinates: List<Double>
)

data class Current(
    val pollution: Pollution,
    val weather: Weather
)

data class Pollution(
    val aqius: Int, // AQI (US)
    val mainus: String, // Main pollutant for US AQI
    val aqicn: Int, // AQI (China)
    val maincn: String // Main pollutant for China AQI
)

data class Weather(
    val ts: String, // Timestamp in ISO 8601 format
    val tp: Int, // Temperature in Celsius
    val pr: Int, // Atmospheric pressure in hPa
    val hu: Int, // Humidity percentage
    val ws: Double, // Wind speed in m/s
    val wd: Int, // Wind direction in degrees
    val ic: String? // Weather icon code
)