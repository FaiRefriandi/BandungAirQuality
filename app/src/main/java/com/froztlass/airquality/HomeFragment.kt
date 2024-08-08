package com.froztlass.airquality

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.froztlass.airquality.model.AirVisualResponse
import com.froztlass.airquality.model.Current
import com.froztlass.airquality.model.Data
import com.froztlass.airquality.model.Location
import com.froztlass.airquality.model.Pollution
import com.froztlass.airquality.model.Weather
import com.froztlass.airquality.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val greetingTextView = view.findViewById<TextView>(R.id.tv_sambutan)
        val cityTextView = view.findViewById<TextView>(R.id.tv_city)

        // ambil username dari SharedPreferences
        val userName = getUserNameFromPreferences()

        // setting username, lanjut sambutan gudmorning
        greetingTextView.text = getGreetingMessage(userName)

        // ngambil data kota dari API
        fetchCityData(cityTextView)

        return view
    }
        //Ambil nama dari akun waktu logan
    private fun getUserNameFromPreferences(): String? {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getString("UserName", "Guest")
    }
        //setting sambutan
    private fun getGreetingMessage(userName: String?): CharSequence {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Night"
        }

        // biar sebelahan username sama sambutannya
        val greetingMessage = "$greeting, $userName!"
        val spannable = SpannableStringBuilder(greetingMessage)

        // biar tebel si sambutannya
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            greeting.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    private fun shouldFetchNewData(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("AirQualityData", Context.MODE_PRIVATE)
        val lastUpdated = sharedPref.getLong("LastUpdated", 0)
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return currentTime - lastUpdated > oneHourInMillis
    }

    private fun fetchCityData(cityTextView: TextView) {
        if (shouldFetchNewData()) {
            // Fetch new data from the API
            val city = "Bandung"
            val state = "West Java"
            val country = "Indonesia"
            val apiKey = RetrofitInstance.API_KEY

            RetrofitInstance.api.getCityData(city, state, country, apiKey)
                .enqueue(object : Callback<AirVisualResponse> {
                    override fun onResponse(
                        call: Call<AirVisualResponse>,
                        response: Response<AirVisualResponse>
                    ) {
                        if (response.isSuccessful) {
                            val airVisualData = response.body()
                            if (airVisualData != null && airVisualData.data != null) {
                                saveCityDataToPreferences(airVisualData)
                                displayCityData(cityTextView, airVisualData)
                            } else {
                                cityTextView.text = "Error: Data is null"
                            }
                        } else {
                            cityTextView.text = "Error fetching data"
                        }
                    }

                    override fun onFailure(call: Call<AirVisualResponse>, t: Throwable) {
                        cityTextView.text = "Error fetching data: ${t.message}"
                    }
                })
        } else {
            // Load data from SharedPreferences
            val airVisualData = getCityDataFromPreferences()
            if (airVisualData != null) {
                displayCityData(cityTextView, airVisualData)
            } else {
                cityTextView.text = "Error: No data available"
            }
        }
    }

    private fun saveCityDataToPreferences(data: AirVisualResponse) {
        val sharedPref = requireActivity().getSharedPreferences("AirQualityData", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("City", data.data?.city)
            putString("State", data.data?.state)
            putString("Country", data.data?.country)
            putInt("AQI", data.data?.current?.pollution?.aqius ?: -1)
            putInt("Temperature", data.data?.current?.weather?.tp ?: -1)
            putInt("Humidity", data.data?.current?.weather?.hu ?: -1)
            putFloat("WindSpeed", data.data?.current?.weather?.ws?.toFloat() ?: -1f)
            putLong("LastUpdated", System.currentTimeMillis())
            apply()
        }
    }

    private fun getCityDataFromPreferences(): AirVisualResponse? {
        val sharedPref = requireActivity().getSharedPreferences("AirQualityData", Context.MODE_PRIVATE)
        val city = sharedPref.getString("City", null)
        val state = sharedPref.getString("State", null)
        val country = sharedPref.getString("Country", null)
        val aqi = sharedPref.getInt("AQI", -1)
        val temperature = sharedPref.getInt("Temperature", -1)
        val humidity = sharedPref.getInt("Humidity", -1)
        val windSpeed = sharedPref.getFloat("WindSpeed", -1f)

        return if (city != null && state != null && country != null) {
            AirVisualResponse(
                status = "success",
                data = Data(
                    city = city,
                    state = state,
                    country = country,
                    location = Location(type = "Point", coordinates = listOf(0.0, 0.0)), // Dummy values for location
                    current = Current(
                        pollution = Pollution(
                            aqius = aqi,
                            mainus = "pm25",
                            aqicn = aqi,
                            maincn = "pm25"
                        ),
                        weather = Weather(
                            ts = "2021-01-01T00:00:00Z", // Dummy timestamp
                            tp = temperature,
                            pr = 1013,
                            hu = humidity,
                            ws = windSpeed.toDouble(),
                            wd = 0,
                            ic = null
                        )
                    )
                )
            )
        } else {
            null
        }
    }

    private fun displayCityData(cityTextView: TextView, airVisualData: AirVisualResponse) {
        val pollution = airVisualData.data?.current?.pollution
        val weather = airVisualData.data?.current?.weather

        cityTextView.text = "Kota: ${airVisualData.data?.city}\n" +
                "Provinsi: ${airVisualData.data?.state}\n" +
                "Negara: ${airVisualData.data?.country}\n" +
                "Tingkat Kualitas Udara: ${pollution?.aqius}\n" +
                "Temperatur: ${weather?.tp}°C\n" +
                "Kelembapan: ${weather?.hu}%\n" +
                "Kecepatan Angin: ${weather?.ws} m/s"
    }
}
