package com.example.coroutineexample

import WeatherData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InlineSuggestionsResponse
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationWeather : AppCompatActivity() {
    private val Key:String="6a1adf3ffd4918763d01d3963817f60b"
    private var temperature: TextView?=null; var windspeed: TextView?=null;var description: TextView?=null;var humidity: TextView?=null;var pressure: TextView?=null;var visibility: TextView?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_weather)
        Intialization()
        val lat= intent.getStringExtra("lat")
        val lon= intent.getStringExtra("lon")
        getReport(lat,lon)
    }

    private fun getReport(lat: String?, lon: String?) {
        val retrofit= getRetrofit.getInstance(ApiInterface.BASE_URL).create(ApiInterface::class.java)
        GlobalScope.launch(Dispatchers.Main){
            val response=retrofit.getCurrentLocationWeatherReport(lat!!,lon!!,"metric",Key)
            if(response.isSuccessful) {
                val weatherData: WeatherData? = response.body()
                setData(weatherData!!)
            }
        }
    }
    private fun Intialization() {
        temperature= findViewById(R.id.temprature);
        windspeed= findViewById(R.id.wind_speed);
        description= findViewById(R.id.desc);
        humidity= findViewById(R.id.humidity);
        pressure= findViewById(R.id.pressure);
        visibility= findViewById(R.id.visibility);
    }
    private fun setData(weatherData: WeatherData) {
        temperature?.setText(weatherData.main?.temp.toString())
        humidity?.setText(weatherData.main?.humidity.toString())
        windspeed?.setText(weatherData.wind?.speed.toString())
        pressure?.setText(weatherData.main?.pressure.toString())
        visibility?.setText(weatherData.visibility.toString())
        description?.setText(weatherData.weather?.get(0)?.description.toString())
    }
}