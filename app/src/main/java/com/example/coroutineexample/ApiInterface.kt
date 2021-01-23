package com.example.coroutineexample

import WeatherData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface ApiInterface {
    companion object{
        val BASE_URL: String
            get() = "https://api.openweathermap.org/"
    }

    @GET()
    suspend fun getRepo():Response<Objects>

    @GET("data/2.5/weather")
    suspend fun getCityReport(
            @Query("q") city:String,
            @Query("units") unit:String,
            @Query("appid") key:String
    ):Response<WeatherData>

    @GET("data/2.5/weather")
    suspend fun getCurrentLocationWeatherReport(@Query("lat") lat:String, @Query("lon") lon:String,
                                                @Query("units") unit:String, @Query("appid") key: String)
            :Response<WeatherData>
}