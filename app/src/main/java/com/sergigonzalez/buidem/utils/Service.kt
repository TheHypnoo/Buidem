package com.sergigonzalez.buidem.utils

import com.sergigonzalez.buidem.data.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface Service {

    @GET
    fun getJSON(@Url url: String): Call<Weather>
}
