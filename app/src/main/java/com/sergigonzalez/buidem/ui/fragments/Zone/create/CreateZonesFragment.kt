package com.sergigonzalez.buidem.ui.fragments.Zone.create

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.FragmentCreateZonesBinding
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateZonesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var _binding: FragmentCreateZonesBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()
    private var zone: Zones? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateZonesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = MachinesApplication.getDatabase(this@CreateZonesFragment.requireContext())
        if (zone != null) {
            edit()
        } else {
            create()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        zone = arguments?.getSerializable("Zone") as Zones?
    }

    fun create() {
        binding.btnCreateZone.setOnClickListener {
            if (binding.editTextZone.text?.isEmpty() == true) {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún nombre para crear la zona",
                    false
                )
            } else {
                val zone = Zones(
                    0,
                    binding.editTextZone.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().insertZones(zone)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createZone_to_ZoneFragment) }
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                    } catch (e: SQLiteConstraintException) {
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                        utilWidgets.snackbarMessage(
                            binding.root,
                            "Ya existe ese nombre en una zona",
                            false
                        )
                    }
                }
            }
        }
    }

    fun edit() {
        binding.editTextZone.setText(zone?.nameZone)
        binding.btnCreateZone.setOnClickListener {
            if (binding.editTextZone.text?.isEmpty() == true) {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                utilWidgets.snackbarMessage(
                    binding.root,
                    "El nombre de la zona esta vacio",
                    false
                )
            } else {
                val zone = Zones(
                    zone!!._id,
                    binding.editTextZone.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().updateZone(zone)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createZone_to_ZoneFragment) }
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                    } catch (e: SQLiteConstraintException) {
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                        utilWidgets.snackbarMessage(
                            binding.root,
                            "Ya existe ese nombre en una zona",
                            false
                        )
                    }
                }
            }
        }
    }
}