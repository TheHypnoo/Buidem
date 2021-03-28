package com.sergigonzalez.buidem.ui.fragments.Maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
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
    private var ZoomToAddress = 15
    private var utilWidgets = util_widgets()
    private lateinit var color: String
    private var listMachines: ArrayList<Machines>? = null
    private var listColors: ArrayList<String>? = null
    private var listidMachines: ArrayList<Int>? = null

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
        println("Acabo de entrar al Maps")
        database = MachinesApplication.getDatabase(this@MapsFragment.requireActivity())
        mMapView = binding.mvMap
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        InitializeMap()
    }

    private fun InitializeMap() {
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (listMachines != null) {
            if (listMachines?.size!! > 0) {
                for (machine in listMachines!!) {
                    CoroutineScope(Dispatchers.IO).launch {
                        listColors?.add(database.MachinesApplication()
                            .searchColorTypeMachine(machine.typeMachine.toInt()).colorTypeMachine)
                        listidMachines?.add(machine.typeMachine.toInt())
                    }
                }
            }
        }
        if (machines != null) {
            val idMachine = machines?.typeMachine?.toInt()

            CoroutineScope(Dispatchers.IO).launch {
                color = database.MachinesApplication()
                    .searchColorTypeMachine(idMachine!!).colorTypeMachine
                println("Prueba color: $color")
            }
        } else {
            color = "#FF5733"
        }
        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            ApplyPermissions()
            if (machines != null) {
                val _mark: LatLng? = getLocationFromAddress(
                    machines!!.addressMachine + " " + machines!!.townMachine + " " + machines!!.postalCodeMachine
                )
                if (_mark != null) {
                    googleMap.addMarker(
                        MarkerOptions().position(_mark).title(machines!!.nameClient).snippet(
                            machines!!.addressMachine + ", " + machines!!.townMachine + ", " + machines!!.postalCodeMachine
                        ).draggable(true).icon(
                            getMarkerIcon(
                                color
                            )
                        )
                    )
                    val cameraPosition =
                        CameraPosition.Builder().target(_mark).zoom(ZoomToAddress.toFloat()).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    utilWidgets.snackbarMessage(binding.root, "Dirección no encontrada", false)
                }
            }
            if (listMachines != null) {
                if (listMachines?.size!! > 0) {
                    for (machine in listMachines!!) {
                        val _mark = getLocationFromAddress(
                            machine.addressMachine + " " + machine.townMachine + " " + machine.postalCodeMachine
                        )

                        if (_mark != null) {
                            googleMap.addMarker(
                                MarkerOptions().position(_mark).title(machine.nameClient)
                                    .snippet(
                                        machine.addressMachine + ", " + machine.townMachine + ", " + machine.postalCodeMachine
                                    ).draggable(true).icon(
                                        getMarkerIcon(
                                            color
                                        )
                                    )
                            )

                            val cameraPosition =
                                CameraPosition.Builder().target(_mark).zoom(5f).build()
                            googleMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    cameraPosition
                                )
                            )
                        }
                    }
                } else {
                    utilWidgets.snackbarMessage(binding.root, "No hay maquinas en la zona", false)
                }
            }
        }

    }

    private fun getLocationFromAddress(_AddressOrZone: String): LatLng? {
        val coder = Geocoder(activity)
        val address: List<Address>
        var _lanlat: LatLng? = null
        try {
            address = coder.getFromLocationName(_AddressOrZone, 5)
            if (address.isEmpty()) {
                return null
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

    private fun ApplyPermissions() {
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