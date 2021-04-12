package com.sergigonzalez.buidem.ui.fragments.Zone

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.FragmentZonesBinding
import com.sergigonzalez.buidem.ui.fragments.Zone.adapter.ZoneAdapter
import com.sergigonzalez.buidem.ui.fragments.Zone.create.CreateZonesFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ZonesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var listZones: List<Zones> = emptyList()
    private var _binding: FragmentZonesBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZonesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Zones"
        database = MachinesApplication.getDatabase(this@ZonesFragment.requireContext())
        binding.faCreateZones.setOnClickListener {
            findNavController().navigate(R.id.action_ZoneFragment_to_createZone)
        }
        assignAdapter()
        getAllZones()
    }

    private fun getAllZones() {
        database.MachinesApplication().getAllZones().observe(viewLifecycleOwner) {
            listZones = it
            binding.rvZones.addItemDecoration(
                DividerItemDecoration(
                    this@ZonesFragment.context,
                    DividerItemDecoration.VERTICAL
                )
            )
            val itemTouchCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder1: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    CoroutineScope(Dispatchers.IO).launch {
                        val checkCreatedZone: Boolean = database.MachinesApplication()
                            .searchZoneinMachine(listZones[position]._id)
                        withContext(Dispatchers.Main) {
                            if (checkCreatedZone) {
                                utilWidgets.snackbarMessage(
                                    binding.root,
                                    "Hay una maquina asignada a esta zona, debes eliminar primero la maquina",
                                    false
                                )
                                binding.rvZones.adapter!!.notifyItemChanged(position)
                            } else {
                                val builder = AlertDialog.Builder(view?.context)

                                builder.setMessage("Estas seguro que deseas eliminar la Zona?\nCon Nombre: ${listZones[position].nameZone}")


                                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        database.MachinesApplication()
                                            .deleteZone(listZones[position])
                                    }
                                    binding.rvZones.adapter!!.notifyItemRemoved(position)
                                }

                                builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                    binding.rvZones.adapter!!.notifyItemChanged(position)
                                }

                                builder.show()
                            }
                        }
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvZones)

            val adapter = ZoneAdapter(listZones)

            binding.rvZones.adapter = adapter
            if (listZones.isEmpty()) {
                binding.tvEmptyZones.visibility = View.VISIBLE
                binding.rvZones.visibility = View.GONE
            } else {
                binding.tvEmptyZones.visibility = View.GONE
                binding.rvZones.visibility = View.VISIBLE
            }
        }
    }

    private fun assignAdapter() {
        binding.rvZones.layoutManager = LinearLayoutManager(this@ZonesFragment.context)
        val adapter = ZoneAdapter(listZones)
        binding.rvZones.adapter = adapter
    }

}