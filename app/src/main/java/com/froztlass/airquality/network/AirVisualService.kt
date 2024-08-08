import com.froztlass.airquality.model.AirVisualResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirVisualService {
    @GET("nearest_city")
    fun getNearestCityData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String
    ): Call<AirVisualResponse>

    @GET("city")
    fun getCityData(
        @Query("city") city: String,
        @Query("state") state: String,
        @Query("country") country: String,
        @Query("key") apiKey: String
    ): Call<AirVisualResponse>
}
