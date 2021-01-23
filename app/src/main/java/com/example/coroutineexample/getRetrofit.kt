package com.example.coroutineexample

import android.accessibilityservice.GestureDescription
import android.os.Build
import androidx.annotation.RequiresApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class getRetrofit {
    var retrofit:Retrofit?=null;
    companion object{
        @JvmStatic
     fun getInstance(url:String):Retrofit{
         var retrofit:Retrofit?=null;
         if(retrofit==null){
            retrofit= Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit!!;
    }
    }
}