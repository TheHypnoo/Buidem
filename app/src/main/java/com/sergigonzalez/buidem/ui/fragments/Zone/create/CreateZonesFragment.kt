package com.sergigonzalez.buidem.ui.fragments.Zone.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.FragmentCreateZonesBinding
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateZonesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var _binding: FragmentCreateZonesBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()

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
        binding.fabSaveCreateZone.setOnClickListener {
            val zone = Zones(
                0,
                binding.etZone.text.toString(),
            )
            CoroutineScope(Dispatchers.IO).launch {
                database.MachinesApplication().insertZones(zone)
            }
        }
    }

}