package com.sergigonzalez.buidem.ui.fragments.weather

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.application.AppConstants
import com.sergigonzalez.buidem.data.model.Weather.Weather
import com.sergigonzalez.buidem.databinding.FragmentWeatherBinding
import com.sergigonzalez.buidem.utils.Service
import com.sergigonzalez.buidem.utils.util_widgets
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()
    var found = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun Search(city: String) {
        found = false
        visibility(visible = false, progressbar = true)
        /*val Dialog = ProgressDialog(activity)
        Dialog.setCancelable(false)
        Dialog.setCanceledOnTouchOutside(false)
        Dialog.setMessage("Descargando datos...")
        Dialog.show()*/
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.BASEAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val httpService = retrofit.create(Service::class.java)

        val call: Call<Weather> =
            httpService.getJSON("weather?q=$city&appid=${AppConstants.API_ID}&lang=${AppConstants.LANG}")

        call.enqueue(object : Callback<Weather> {

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                t.message?.let {
                    println(it)
                    utilWidgets.snackbarMessage(binding.root, "Error Weather: $it", false)
                    visibility(visible = false, progressbar = false)
                }
                //Dialog.hide()

            }


            override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {

                if (!response!!.isSuccessful) {
                    utilWidgets.snackbarMessage(binding.root, "Error Weather: ${response.code().toString()}", false)
                    //Dialog.hide()
                    visibility(visible = false, progressbar = false)
                    return

                }
                //Dialog.setMessage("Procesando datos...")
                val cityList = response.body()

                if (cityList != null) {

                    binding.tvNameCity.text = "${cityList.name}"
                    binding.tvTempMax.text =
                        "${utilWidgets.convertFahreheit(cityList.main.tempMax).toInt()} º"
                    binding.tvTempMin.text =
                        "${utilWidgets.convertFahreheit(cityList.main.tempMin).toInt()} º"

                    binding.tvTempNormal.text =
                        "${utilWidgets.convertFahreheit(cityList.main.temp).toInt()} º"
                    /*
                    binding.tvWeather.text = cityList.weather[0].description

                    binding.tvFeel.text =
                        "Sensacion termica: ${Convert(cityList.main.feelsLike).toInt()} º"
                    binding.tvPres.text = "Presion: ${cityList.main.pressure} mb"
                    binding.tvHum.text = "Humedad: ${cityList.main.humidity}"
                    binding.tvAlgo.text = "Visibilidad: ${cityList.visibility}"
                    binding.tvWind.text = "Vel.Viento ${cityList.wind.speed}"*/
                    val uri =
                        URL("https://openweathermap.org/img/wn/" + cityList.weather[0].icon + "@2x.png")
                    val bmp = BitmapFactory.decodeStream(uri.openConnection().getInputStream())
                    binding.imgIcon.setImageBitmap(bmp)

                    //Dialog.hide()
                    Handler(Looper.myLooper()!!).postDelayed({
                        visibility(visible = true, progressbar = false)
                        found = true
                    }, 1500)
                }
            }
        }
        )


    }

    fun visibility(visible: Boolean, progressbar: Boolean) {
        when {
            visible -> {
                binding.llWeather.visibility = View.VISIBLE
                binding.cpiWeather.visibility = View.GONE
            }
            progressbar -> {
                binding.llWeather.visibility = View.GONE
                binding.cpiWeather.visibility = View.VISIBLE
            }
            else -> {
                binding.llWeather.visibility = View.GONE
                binding.cpiWeather.visibility = View.GONE
            }
        }
    }

}