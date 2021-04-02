package com.sergigonzalez.buidem.ui.fragments.weather

import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.sergigonzalez.buidem.data.model.Weather
import com.sergigonzalez.buidem.databinding.FragmentWeatherBinding
import com.sergigonzalez.buidem.utils.Service
import com.sergigonzalez.buidem.utils.util_widgets
import org.json.JSONObject
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
    val BASEAPI = "http://api.openweathermap.org/data/2.5/"
    val API_ID = "5ab85ccd70b8d88070aa1c166d5006bd"
    val LANG = "es"

    private val _temp = 0
    private val _tempMax = 0
    private val _tempMin = 0
    private var city: String? = null
    private val urlIcon: String? = null

    private val _weather: JSONObject? = null

    private val root: View? = null

    private val tvTemp: TextView? = null
    private val tvTempMax: TextView? = null
    private val tvTempMin: TextView? = null
    private val tvCity: TextView? = null

    private val imgIcon: ImageView? = null

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

    fun Search(city: String, activity: FragmentActivity) {
        val Dialog = ProgressDialog(activity)
        Dialog.setCancelable(false)
        Dialog.setCanceledOnTouchOutside(false)
        Dialog.setMessage("Descargando datos...")
        Dialog.show()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASEAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val httpService = retrofit.create(Service::class.java)

        val call: Call<Weather> =
            httpService.getJSON("weather?q=$city&appid=${API_ID}&lang=${LANG}")

        call.enqueue(object : Callback<Weather> {

            override fun onFailure(call: Call<Weather>, t: Throwable) {
                t.message?.let {
                    println(it)
                    utilWidgets.snackbarMessage(binding.root, it, false)
                }
                Dialog.hide()

            }


            override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {

                if (!response!!.isSuccessful) {
                    utilWidgets.snackbarMessage(binding.root, response.code().toString(), false)
                    Dialog.hide()
                    return

                }
                Dialog.setMessage("Procesando datos...")
                val cityList = response.body()

                if (cityList != null) {
/*
                    binding.tvNameCity.text = "Tiempo actual en ${cityList.name}"
                    binding.tvTempmax.text =
                        "${utilWidgets.convertFahreheit(cityList.main.tempMax).toInt()} º"
                    binding.tvTemMin.text =
                        "${utilWidgets.convertFahreheit(cityList.main.tempMin).toInt()} º"

                    binding.tvTempNormal.text =
                        "${utilWidgets.convertFahreheit(cityList.main.temp).toInt()} º"*/
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
                    //binding.imgIcon.setImageBitmap(bmp)
                    //binding.edtCiudad.text = Editable.Factory.getInstance().newEditable("")
                    //visibility(View.VISIBLE)
                    Dialog.hide()
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        city = arguments?.getString("City")
    }

}