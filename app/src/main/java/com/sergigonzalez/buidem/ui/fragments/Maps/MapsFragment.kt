package com.sergigonzalez.buidem.ui.fragments.Maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.FragmentMapsBinding
import com.sergigonzalez.buidem.ui.fragments.weather.WeatherFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: MachinesApplication
    lateinit var mMapView: MapView
    private lateinit var googleMap: GoogleMap
    private var machines: Machines? = null
    private var zoomToAddress = 18
    private var utilWidgets = util_widgets()
    private lateinit var color: String
    private var listMachines: ArrayList<Machines>? = arrayListOf()
    private var listColors: ArrayList<String> = arrayListOf()
    private var contador = 0
    private var fragmentWeather = WeatherFragment()
    private var nameTypeMachine = ""
    private var listNameTypeMachine: ArrayList<String> = arrayListOf()
    private var listMarkers: ArrayList<LatLng> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = MachinesApplication.getDatabase(this@MapsFragment.requireActivity())
        fragmentWeather =
            (childFragmentManager.findFragmentByTag("fragWeather") as? WeatherFragment)!!
        fragmentWeather.visibility(visible = false, progressbar = true, false)
        mMapView = binding.mvMap
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        if (isNetworkAvailable()) {
            CoroutineScope(Dispatchers.IO).launch {
                getColorsAndNamesTypeMachine()
            }
            startMap()
            if (machines != null) {
                fragmentWeather.Search(machines!!.townMachine)
            } else if (listMachines != null && listMachines!!.size > 0) {
                fragmentWeather.Search(listMachines!![0].townMachine)
            } else {
                fragmentWeather.visibility(visible = false, progressbar = false, false)
            }
        } else {
            binding.tvNoInternet.visibility = View.VISIBLE
            binding.mvMap.visibility = View.GONE
        }
    }

    private fun startMap() {
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            applyPermissions()
            lifecycleScope.launch { markers() }
            googleMap.setOnMarkerClickListener(this)
        }
    }

    private fun getLocationFromAddress(_AddressOrZone: String): LatLng {
        val coder = Geocoder(activity)
        val address: List<Address>
        var _lanlat: LatLng
        try {
            address = coder.getFromLocationName(_AddressOrZone, 5)
            if (address.isEmpty()) {
                return LatLng(0.0, 0.0)
            }
            val location = address[0]
            location.latitude
            location.longitude
            _lanlat = LatLng(
                location.latitude,
                location.longitude
            )
        } catch (e: IOException) {
            _lanlat = LatLng(
                0.0,
                0.0
            )
        }
        return _lanlat
    }

    private fun applyPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                requireActivity(),
                "Ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    private fun getMarkerIcon(color: String): BitmapDescriptor? {
        val hsv = FloatArray(3)
        Color.colorToHSV(Color.parseColor(color), hsv)
        return BitmapDescriptorFactory.defaultMarker(hsv[0])
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 30 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    private suspend fun getColorsAndNamesTypeMachine() {
        if (listMachines != null && listMachines?.size!! > 0) {
            for (i in listMachines!!.indices) {
                color = withContext(Dispatchers.IO) {
                    database.MachinesApplication()
                        .searchColorTypeMachine(listMachines!![i].typeMachine).colorTypeMachine
                }
                nameTypeMachine = withContext(Dispatchers.IO) {
                    database.MachinesApplication()
                        .searchTypeMachinebyID(listMachines!![i].typeMachine).nameTypeMachine
                }
                listNameTypeMachine.add(nameTypeMachine)
                listColors.add(color)
            }
        } else if (machines != null) {
            nameTypeMachine = withContext(Dispatchers.IO) {
                database.MachinesApplication()
                    .searchTypeMachinebyID(machines!!.typeMachine).nameTypeMachine
            }
            color = withContext(Dispatchers.IO) {
                database.MachinesApplication()
                    .searchColorTypeMachine(machines!!.typeMachine).colorTypeMachine
            }
        }
    }

    private fun calculateZoom(){
        if(listMarkers.size > 0) {
            val builder = LatLngBounds.builder()
            for(marker in listMarkers){
                builder.include(marker)
            }
            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun markers() {
        if (machines != null) {
            val marker: LatLng = getLocationFromAddress(
                machines!!.addressMachine + " " + machines!!.townMachine + " " + machines!!.postalCodeMachine
            )
            if (marker.latitude != 0.0 && marker.latitude != 0.0) {
                googleMap.addMarker(
                    MarkerOptions().position(marker)
                        .title(machines!!.nameClient + ", " + machines!!.serialNumberMachine + ", " + nameTypeMachine)
                        .snippet(
                            machines!!.addressMachine + ", " + machines!!.townMachine + ", " + machines!!.postalCodeMachine
                        ).draggable(true).icon(
                            getMarkerIcon(
                                color
                            )
                        )
                )
               val cameraPosition =
                    CameraPosition.Builder().target(marker).zoom(zoomToAddress.toFloat())
                        .build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                Handler(Looper.myLooper()!!).postDelayed({
                    utilWidgets.snackbarMessage(
                        binding.root,
                        "No se ha encontrado esa maquina",
                        false
                    )
                }, 1500)
            }
        } else if (listMachines != null) {
            if (listMachines?.size!! > 0) {
                for (machine in listMachines!!) {
                    val marker = getLocationFromAddress(
                        machine.addressMachine + " " + machine.townMachine + " " + machine.postalCodeMachine
                    )
                    if (marker.latitude != 0.0 && marker.latitude != 0.0) {
                        googleMap.addMarker(
                            MarkerOptions().position(marker)
                                .title(machine.nameClient + ", " + machine.serialNumberMachine + ", " + listNameTypeMachine[contador])
                                .snippet(
                                    machine.addressMachine + ", " + machine.townMachine + ", " + machine.postalCodeMachine
                                ).draggable(true).icon(
                                    getMarkerIcon(
                                        listColors[contador]
                                    )
                                )
                        )
                        contador++
                        listMarkers.add(marker)
                    } else {
                        Handler(Looper.myLooper()!!).postDelayed({
                            utilWidgets.snackbarMessage(
                                binding.root,
                                "No se ha encontrado maquina con SerialNumber: ${machine.serialNumberMachine}",
                                false
                            )
                        }, 1500)
                    }
                }
                if(listMarkers.isNotEmpty()) {
                    calculateZoom()
                }
            } else {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No hay maquinas en la zona",
                    false
                )
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val town = p0!!.snippet.split(",")
        fragmentWeather.Search(town[1])
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        machines = arguments?.getSerializable("Machine") as Machines?
        listMachines = arguments?.getSerializable("listMachines") as ArrayList<Machines>?
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}