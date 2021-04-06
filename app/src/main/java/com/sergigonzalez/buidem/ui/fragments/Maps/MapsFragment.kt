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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.FragmentMapsBinding
import com.sergigonzalez.buidem.ui.fragments.weather.WeatherFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: MachinesApplication
    lateinit var mMapView: MapView
    private lateinit var googleMap: GoogleMap
    private var machines: Machines? = null
    private var zoomToAddress = 15
    private var utilWidgets = util_widgets()
    private lateinit var color: String
    private var listMachines: ArrayList<Machines>? = arrayListOf()
    private var listColors: ArrayList<String> = arrayListOf()
    private var listMachineID: ArrayList<Int> = arrayListOf()
    private var contador = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Maps"
        database = MachinesApplication.getDatabase(this@MapsFragment.requireActivity())
        mMapView = binding.mvMap
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        if (isNetworkAvailable()) {
            val fragmentWeather =
                childFragmentManager.findFragmentByTag("fragWeather") as? WeatherFragment
                    ?: return
            fragmentWeather.view?.visibility = View.GONE
            getColors().runCatching { startMap() }.runCatching {
                if (machines != null) {
                    activity?.let { fragmentWeather.Search(machines!!.townMachine, it) }
                        .runCatching { fragmentWeather.view?.visibility = View.VISIBLE }
                } else if (listMachines != null && listMachines!!.size > 0) {
                    activity?.let { fragmentWeather.Search(listMachines!![0].townMachine, it) }
                        .runCatching { fragmentWeather.view?.visibility = View.VISIBLE }
                } else {
                    fragmentWeather.view?.visibility = View.GONE
                }
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
            applyPermissions()
            markers()
        }
    }

    private fun getLocationFromAddress(_AddressOrZone: String): LatLng {
        val coder = Geocoder(activity)
        val address: List<Address>
        var _lanlat: LatLng
        try {
            address = coder.getFromLocationName(_AddressOrZone, 5)
            if (address.isEmpty()) {
                utilWidgets.snackbarMessage(binding.root, "La dirección esta vacia", false)
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
                0.toDouble(),
                0.toDouble()
            )
        }
        return _lanlat
    }

    private fun applyPermissions() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
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

    private fun getColors() {
        if (listMachines != null) {
            if (listMachines?.size!! > 0) {
                for (i in listMachines!!.indices) {
                    CoroutineScope(Dispatchers.IO).launch {
                        color = database.MachinesApplication()
                            .searchColorTypeMachine(listMachines!![i].typeMachine).colorTypeMachine
                    }.invokeOnCompletion {
                        listColors.add(color)
                        listMachineID.add(listMachines!![i]._id)
                    }
                }
            }
        } else if (machines != null) {
            val idMachine = machines?.typeMachine

            CoroutineScope(Dispatchers.IO).launch {
                color = database.MachinesApplication()
                    .searchColorTypeMachine(idMachine!!).colorTypeMachine
            }
        }
    }

    private fun markers() {
        if (machines != null) {
            val marker: LatLng = getLocationFromAddress(
                machines!!.addressMachine + " " + machines!!.townMachine + " " + machines!!.postalCodeMachine
            )
            googleMap.addMarker(
                MarkerOptions().position(marker).title(machines!!.nameClient).snippet(
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
        } else if (listMachines != null) {
            println("Maquinas: ${listMachines!!.size}")
            if (listMachines?.size!! > 0) {
                for (machine in listMachines!!) {
                    val marker = getLocationFromAddress(
                        machine.addressMachine + " " + machine.townMachine + " " + machine.postalCodeMachine
                    )

                    if (machine._id == listMachineID[contador]) {
                        googleMap.addMarker(
                            MarkerOptions().position(marker).title(machine.nameClient)
                                .snippet(
                                    machine.addressMachine + ", " + machine.townMachine + ", " + machine.postalCodeMachine
                                ).draggable(true).icon(
                                    getMarkerIcon(
                                        listColors[contador]
                                    )
                                )
                        )
                    }
                    val cameraPosition =
                        CameraPosition.Builder().target(marker).zoom(10f).build()
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            cameraPosition
                        )
                    )
                    ++contador
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