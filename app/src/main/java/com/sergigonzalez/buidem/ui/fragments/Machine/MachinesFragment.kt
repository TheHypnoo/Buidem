package com.sergigonzalez.buidem.ui.fragments.Machine

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.FragmentMachinesBinding
import com.sergigonzalez.buidem.ui.fragments.Machine.adapter.MachineAdapter
import com.sergigonzalez.buidem.ui.fragments.Machine.create.CreateMachineFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MachinesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var listMachines: List<Machines> = emptyList()
    private var _binding: FragmentMachinesBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMachinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Home"
        database = MachinesApplication.getDatabase(this@MachinesFragment.requireContext())
        binding.faCreateMachine.setOnClickListener {
            utilWidgets.replaceFragment(
                CreateMachineFragment(),
                requireActivity()
            )
        }
        assignAdapter()
        getAllMachines()

    }

    private fun getAllMachines() {
        database.MachinesApplication().getAllMachines().observe(viewLifecycleOwner) {
            listMachines = it
            binding.rvMachines.addItemDecoration(
                DividerItemDecoration(
                    this@MachinesFragment.context,
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
                    val builder = AlertDialog.Builder(view?.context)
                    val position = viewHolder.adapterPosition
                    builder.setMessage("Estas seguro que deseas eliminar la Maquina?\nNombre del Cliente: ${listMachines[position].nameClient}\nNº de Serie:  ${listMachines[position].serialNumberMachine}")


                    builder.setPositiveButton(android.R.string.ok) { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            database.MachinesApplication().deleteMachine(listMachines[position])
                        }
                        binding.rvMachines.adapter!!.notifyItemRemoved(position)
                    }

                    builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                        binding.rvMachines.adapter!!.notifyItemChanged(position)
                    }

                    builder.show()

                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvMachines)

            val adapter = MachineAdapter(listMachines)

            binding.rvMachines.adapter = adapter
            if (listMachines.isEmpty()) {
                binding.tvEmptyMachines.visibility = View.VISIBLE
                binding.rvMachines.visibility = View.GONE
            } else {
                binding.tvEmptyMachines.visibility = View.GONE
                binding.rvMachines.visibility = View.VISIBLE
            }
        }
    }


    private fun assignAdapter() {
        binding.rvMachines.layoutManager = LinearLayoutManager(this@MachinesFragment.context)
        val adapter = MachineAdapter(listMachines)
        binding.rvMachines.adapter = adapter
    }
}