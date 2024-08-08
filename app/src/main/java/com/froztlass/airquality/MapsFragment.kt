package com.froztlass.airquality

import AirVisualService
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.froztlass.airquality.model.AirVisualResponse
import com.froztlass.airquality.network.RetrofitInstance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var mapStateBundle: Bundle? = null
    private val markers = mutableSetOf<MarkerOptions>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ambil sambutan hari dari kalender
        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)

        // biar ga keliatan si kalendernya
        calendarView.isEnabled = false

        // ngambil data hari real-time
        val calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis

        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(currentDate))

        val builder = SpannableStringBuilder()

        builder.append("Today is ")

        val dateStartIndex = builder.length
        builder.append(formattedDate)
        builder.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            dateStartIndex,
            builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        dateTextView.text = builder

        // ngatur biar ambil tanggal hari ini
        calendarView.date = currentDate

        mapView = view.findViewById(R.id.mapView)
        mapStateBundle = savedInstanceState?.getBundle("mapState")
        mapView.onCreate(mapStateBundle)
        mapView.getMapAsync(this)

        val aqiInfoButton: Button = view.findViewById(R.id.aqi_info_button)
        aqiInfoButton.setOnClickListener {
            showAqiInfoDialog()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // buat mulihin marker (suka ilang kalo ganti fragment atau aplikasinya kena close)
        val sharedPreferences = requireContext().getSharedPreferences("MapPrefs", Context.MODE_PRIVATE)
        val markerStrings = sharedPreferences.getStringSet("markers", null)
        markerStrings?.forEach { markerData ->
            val parts = markerData.split(",")
            val lat = parts[0].toDouble()
            val lng = parts[1].toDouble()
            val title = parts[2]
            val snippet = if (parts.size > 3) parts[3] else ""
            val latLng = LatLng(lat, lng)
            googleMap.addMarker(MarkerOptions().position(latLng).title(title).snippet(snippet))
        }

        // setting kamera ke lokasi terakhir yang diliat
        val cameraPosition = sharedPreferences.getString("cameraPosition", null)?.split(",")
        if (cameraPosition != null && cameraPosition.size == 3) {
            val lat = cameraPosition[0].toDouble()
            val lng = cameraPosition[1].toDouble()
            val zoom = cameraPosition[2].toFloat()
            val position = CameraPosition(LatLng(lat, lng), zoom, 0f, 0f)
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
        } else {
            // default posisi kamera di Bandung
            val defaultPosition = LatLng(-6.9175, 107.6191)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 10f))
        }

        // panggil ulang api kalo gada markernya
        if (markers.isEmpty() || shouldFetchNewData()) {
            fetchAndDisplayMarkers()
        }
    }

    //setting biar updatenya setiap 1 jam
    private fun shouldFetchNewData(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("MapPrefs", Context.MODE_PRIVATE)
        val lastUpdated = sharedPref.getLong("LastUpdated", 0)
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return currentTime - lastUpdated > oneHourInMillis
    }

    // ngambil data dari api (ini datanya dah ada di json)
    private fun fetchAndDisplayMarkers() {
        val apiKey = RetrofitInstance.API_KEY
        val locations = listOf(
            Triple("Bandung", "West Java", "Indonesia")
        )
    // kalo ini datanya harus masukkin manual, karena idnya gada di json
        val manualLocations = listOf(
            Pair("Cimahi", LatLng(-6.8720, 107.5429)),
            Pair("Lembang", LatLng(-6.8213, 107.6064)),
            Pair("Rancaekek", LatLng(-6.973360, 107.775467)),
            Pair("Soreang", LatLng(-7.025253, 107.519760))
        )
        val service = RetrofitInstance.retrofit.create(AirVisualService::class.java)
    // atas ini khusus lokasi yang ada di json
        for (location in locations) {
            val (city, state, country) = location
            val call = service.getCityData(city, state, country, apiKey)
            call.enqueue(object : Callback<AirVisualResponse> {
                override fun onResponse(call: Call<AirVisualResponse>, response: Response<AirVisualResponse>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
                        val lat = data?.location?.coordinates?.get(1)
                        val lon = data?.location?.coordinates?.get(0)
                        val aqi = data?.current?.pollution?.aqius
                        val tp = data?.current?.weather?.tp
                        val hu = data?.current?.weather?.hu

                        Log.d("API Response", "Received data: lat=$lat, lon=$lon, aqi=$aqi, tp=$tp, hu=$hu")

                        if (lat != null && lon != null && aqi != null && tp != null && hu != null) {
                            val latLng = LatLng(lat, lon)
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .title("Kualitas Udara: $aqi di $city")
                                .snippet("Temperatur: $tp°C, Kelembapan : $hu%")
                            googleMap.addMarker(markerOptions)
                            markers.add(markerOptions)
                            saveMarkersToPreferences()
                            if (city == "Bandung") {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                            }
                        } else {
                            Log.e("API Response", "Missing data for city: $city")
                        }
                    } else {
                        Log.e("API Response", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AirVisualResponse>, t: Throwable) {
                    Log.e("API Response", "API call failed for city: $city, error: ${t.message}")
                }
            })
        }
    //inimah yang gada di json
        for (manualLocation in manualLocations) {
            val (city, latLng) = manualLocation
            val call = service.getNearestCityData(latLng.latitude, latLng.longitude, apiKey)
            call.enqueue(object : Callback<AirVisualResponse> {
                override fun onResponse(call: Call<AirVisualResponse>, response: Response<AirVisualResponse>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
                        val aqi = data?.current?.pollution?.aqius
                        val tp = data?.current?.weather?.tp
                        val hu = data?.current?.weather?.hu

                        Log.d("API Response", "Received manual location data: city=$city, aqi=$aqi, tp=$tp, hu=$hu")

                        if (aqi != null && tp != null && hu != null) {
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .title("Kualitas Udara: $aqi di $city")
                                .snippet("Temperatur: $tp°C, Kelembapan : $hu%")
                            googleMap.addMarker(markerOptions)
                            markers.add(markerOptions)
                            saveMarkersToPreferences()
                        } else {
                            Log.e("API Response", "Missing data for manual city: $city")
                        }
                    } else {
                        Log.e("API Response", "Response not successful for manual city: $city, error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AirVisualResponse>, t: Throwable) {
                    Log.e("API Response", "API call failed for manual city: $city, error: ${t.message}")
                }
            })
        }

        // eksekusi update
        val sharedPref = requireActivity().getSharedPreferences("MapPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("LastUpdated", System.currentTimeMillis())
            apply()
        }
    }

    private fun saveMarkersToPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("MapPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val markerStrings = markers.map { marker ->
            val latLng = marker.position
            val snippet = marker.snippet
            "${latLng.latitude},${latLng.longitude},${marker.title},$snippet"
        }.toSet()
        editor.putStringSet("markers", markerStrings)

        val cameraPosition = googleMap.cameraPosition
        val cameraPositionString = "${cameraPosition.target.latitude},${cameraPosition.target.longitude},${cameraPosition.zoom}"
        editor.putString("cameraPosition", cameraPositionString)
        editor.apply()
    }

    private fun showAqiInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_aqi_info, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapState = Bundle()
        mapView.onSaveInstanceState(mapState)
        outState.putBundle("mapState", mapState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}