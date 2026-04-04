package com.froztlass.airquality

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var tvSambutan: TextView? = null
    private var tvLocationLabel: TextView? = null
    private var tvAqiValue: TextView? = null
    private var tvAqiStatus: TextView? = null
    private var tvAqiDesc: TextView? = null
    private var tvTemperature: TextView? = null
    private var tvHumidity: TextView? = null
    private var tvWindSpeed: TextView? = null
    private var tvPressure: TextView? = null
    private var tvPollutantName: TextView? = null
    private var tvPollutantDesc: TextView? = null
    private var tvLastUpdated: TextView? = null
    private var aqiCircleBg: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Bind views
        tvSambutan = view.findViewById(R.id.tv_sambutan)
        tvLocationLabel = view.findViewById(R.id.tv_location_label)
        tvAqiValue = view.findViewById(R.id.tv_aqi_value)
        tvAqiStatus = view.findViewById(R.id.tv_aqi_status)
        tvAqiDesc = view.findViewById(R.id.tv_aqi_desc)
        tvTemperature = view.findViewById(R.id.tv_temperature)
        tvHumidity = view.findViewById(R.id.tv_humidity)
        tvWindSpeed = view.findViewById(R.id.tv_wind_speed)
        tvPressure = view.findViewById(R.id.tv_pressure)
        tvPollutantName = view.findViewById(R.id.tv_pollutant_name)
        tvPollutantDesc = view.findViewById(R.id.tv_pollutant_desc)
        tvLastUpdated = view.findViewById(R.id.tv_last_updated)
        aqiCircleBg = view.findViewById(R.id.aqi_circle_bg)

        // Username greeting
        val userName = getUserNameFromPreferences()
        tvSambutan?.text = getGreetingMessage(userName)

        // Fetch air quality data
        fetchCityData()

        // Entrance animations
        animateEntrance(view)

        return view
    }

    private fun animateEntrance(view: View) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun getUserNameFromPreferences(): String? {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getString("UserName", "Guest")
    }

    private fun getGreetingMessage(userName: String?): CharSequence {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Night"
        }

        val greetingMessage = "$greeting, $userName! 👋"
        val spannable = SpannableStringBuilder(greetingMessage)

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

    private fun fetchCityData() {
        if (shouldFetchNewData()) {
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
                                displayCityData(airVisualData)
                            } else {
                                showErrorState("Data tidak tersedia")
                            }
                        } else {
                            showErrorState("Gagal mengambil data")
                        }
                    }

                    override fun onFailure(call: Call<AirVisualResponse>, t: Throwable) {
                        showErrorState("Koneksi gagal")
                    }
                })
        } else {
            val airVisualData = getCityDataFromPreferences()
            if (airVisualData != null) {
                displayCityData(airVisualData)
            } else {
                showErrorState("Data tidak tersedia")
            }
        }
    }

    private fun showErrorState(message: String) {
        tvAqiValue?.text = "!"
        tvAqiStatus?.text = "Error"
        tvAqiDesc?.text = message
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
            putInt("Pressure", data.data?.current?.weather?.pr ?: -1)
            putString("MainPollutant", data.data?.current?.pollution?.mainus ?: "")
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
        val pressure = sharedPref.getInt("Pressure", 1013)

        return if (city != null && state != null && country != null) {
            AirVisualResponse(
                status = "success",
                data = Data(
                    city = city,
                    state = state,
                    country = country,
                    location = Location(type = "Point", coordinates = listOf(0.0, 0.0)),
                    current = Current(
                        pollution = Pollution(
                            aqius = aqi,
                            mainus = sharedPref.getString("MainPollutant", "pm25") ?: "pm25",
                            aqicn = aqi,
                            maincn = "pm25"
                        ),
                        weather = Weather(
                            ts = "",
                            tp = temperature,
                            pr = pressure,
                            hu = humidity,
                            ws = windSpeed.toDouble(),
                            wd = 0,
                        )
                    )
                )
            )
        } else {
            null
        }
    }

    private fun displayCityData(airVisualData: AirVisualResponse) {
        val data = airVisualData.data ?: return
        val pollution = data.current.pollution
        val weather = data.current.weather
        val aqi = pollution.aqius

        // Location label
        tvLocationLabel?.text = "${data.city}, ${data.state}"

        // AQI value with count-up animation
        animateAqiValue(aqi)

        // AQI status & description
        val aqiInfo = getAqiInfo(aqi)
        tvAqiStatus?.text = aqiInfo.status
        tvAqiStatus?.setTextColor(aqiInfo.color)
        tvAqiDesc?.text = aqiInfo.description

        // Update status pill background color
        updateStatusPillColor(aqiInfo.color)

        // Update AQI circle color
        updateAqiCircleColor(aqiInfo.color)

        // Weather details
        tvTemperature?.text = "${weather.tp}°C"
        tvHumidity?.text = "${weather.hu}%"
        tvWindSpeed?.text = String.format("%.1f m/s", weather.ws)
        tvPressure?.text = "${weather.pr} hPa"

        // Pollutant info
        val pollutantInfo = getPollutantInfo(pollution.mainus)
        tvPollutantName?.text = pollutantInfo.first
        tvPollutantDesc?.text = pollutantInfo.second

        // Last updated
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id"))
        tvLastUpdated?.text = "Terakhir diperbarui: ${dateFormat.format(Date())}"
    }

    private fun animateAqiValue(targetAqi: Int) {
        val animator = ValueAnimator.ofInt(0, targetAqi)
        animator.duration = 1200
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            tvAqiValue?.text = (animation.animatedValue as Int).toString()
        }
        animator.start()
    }

    private fun updateStatusPillColor(color: Int) {
        val bg = tvAqiStatus?.background
        if (bg is GradientDrawable) {
            val alphaColor = (0x33 shl 24) or (color and 0x00FFFFFF)
            val strokeColor = (0x55 shl 24) or (color and 0x00FFFFFF)
            bg.setColor(alphaColor)
            bg.setStroke(1, strokeColor)
        }
    }

    private fun updateAqiCircleColor(color: Int) {
        val bg = aqiCircleBg?.background
        if (bg is GradientDrawable) {
            val alphaColor = (0x2A shl 24) or (color and 0x00FFFFFF)
            val strokeColor = (0x80 shl 24) or (color and 0x00FFFFFF)
            bg.mutate()
            (bg as GradientDrawable).setColor(alphaColor)
            bg.setStroke(3, strokeColor)
        }
    }

    data class AqiInfo(val status: String, val color: Int, val description: String)

    private fun getAqiInfo(aqi: Int): AqiInfo {
        return when {
            aqi <= 50 -> AqiInfo(
                "Baik ✅",
                0xFF69F0AE.toInt(),
                "Kualitas udara baik. Cocok untuk aktivitas luar ruangan."
            )
            aqi <= 100 -> AqiInfo(
                "Sedang ⚠️",
                0xFFFFD54F.toInt(),
                "Kualitas udara cukup. Kelompok sensitif perlu berhati-hati."
            )
            aqi <= 150 -> AqiInfo(
                "Tidak Sehat untuk Sensitif 😷",
                0xFFFF8A65.toInt(),
                "Dapat berdampak pada kelompok sensitif seperti anak-anak dan lansia."
            )
            aqi <= 200 -> AqiInfo(
                "Tidak Sehat 🚫",
                0xFFEF5350.toInt(),
                "Semua orang bisa terdampak. Batasi aktivitas luar ruangan."
            )
            aqi <= 300 -> AqiInfo(
                "Sangat Tidak Sehat ☠️",
                0xFFAB47BC.toInt(),
                "Peringatan kesehatan. Hindari aktivitas luar ruangan."
            )
            else -> AqiInfo(
                "Berbahaya 💀",
                0xFFB71C1C.toInt(),
                "Darurat kesehatan! Seluruh populasi berisiko."
            )
        }
    }

    private fun getPollutantInfo(pollutant: String): Pair<String, String> {
        return when (pollutant.lowercase()) {
            "p2", "pm25" -> Pair("PM2.5", "Partikel halus <2.5μm, berbahaya bagi saluran pernapasan")
            "p1", "pm10" -> Pair("PM10", "Partikel kasar <10μm, dapat mengiritasi saluran pernapasan")
            "o3" -> Pair("Ozon (O₃)", "Gas yang dapat menyebabkan iritasi pada saluran pernapasan")
            "n2", "no2" -> Pair("Nitrogen Dioksida (NO₂)", "Gas dari kendaraan bermotor, menyebabkan peradangan paru")
            "s2", "so2" -> Pair("Sulfur Dioksida (SO₂)", "Gas dari industri, mengiritasi mata dan saluran napas")
            "co" -> Pair("Karbon Monoksida (CO)", "Gas tidak berwarna yang mengurangi oksigen dalam darah")
            else -> Pair(pollutant.uppercase(), "Polutan udara utama di lokasi ini")
        }
    }
}
