package com.example.coroutineexample

import WeatherData
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity(),CoroutineScope by MainScope() {
    private val REQUEST_CHECK_SETTINGS: Int=1
    private val REQUEST_CODE=123
    private var next:Button?=null
    private var temperature:TextView?=null; var windspeed:TextView?=null;var description:TextView?=null;var humidity:TextView?=null;var pressure:TextView?=null;var visibility:TextView?=null
    private val Key:String="6a1adf3ffd4918763d01d3963817f60b"
    private var fusedLocationClient: FusedLocationProviderClient?=null
    private var locationCallback:LocationCallback?=null
    private var lat:String = ""; var lon:String = ""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Intialization();
        getPermission();
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                lat = locationResult.lastLocation.latitude.toString()
                lon = locationResult.lastLocation.longitude.toString()
                next!!.setVisibility(View.VISIBLE);
                fusedLocationClient!!.removeLocationUpdates(locationCallback)
            }
        }
        getData();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermission() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            enableGps();
        }
        else{
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==REQUEST_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
            enableGps();
        }
        else{
            Toast.makeText(this,"Permission Required!! Please Grant",Toast.LENGTH_LONG).show()
            getPermission()
        }
    }

    private fun enableGps() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            setUpLocationListener()
        }


        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    val resolvable = e as ResolvableApiException
                    resolvable.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: SendIntentException) {
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create()
        locationRequest.setInterval(4000)
                .setFastestInterval(2000).priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun Intialization() {
        temperature= findViewById(R.id.tvsetTemp);
        windspeed= findViewById(R.id.tvset_wind_speed);
        description= findViewById(R.id.tvsetdesc);
        humidity= findViewById(R.id.tvsetHumidity);
        pressure= findViewById(R.id.tvsetPressure);
        visibility= findViewById(R.id.tvsetvisibility);
        next= findViewById(R.id.button)
    }

    private fun getData() {
        val retrofit= getRetrofit.getInstance(ApiInterface.BASE_URL).create(ApiInterface::class.java)
        launch(Dispatchers.Main){
            val response=retrofit.getCityReport("Delhi","metric",Key)
            if(response.isSuccessful){
                setData(response)
            }
        }
    }

    private fun setData(response: Response<WeatherData>) {
        temperature?.setText(response.body()?.main?.temp.toString())
        humidity?.setText(response.body()?.main?.humidity.toString())
        windspeed?.setText(response.body()?.wind?.speed.toString())
        pressure?.setText(response.body()?.main?.pressure.toString())
        visibility?.setText(response.body()?.visibility.toString())
        description?.setText(response.body()?.weather?.get(0)?.description.toString())
    }

    fun getWeatherReport(view :View){
        val intent = Intent(this, LocationWeather::class.java)
        intent.putExtra("lat",lat)
        intent.putExtra("lon",lon)
        startActivity(intent)
    }

}