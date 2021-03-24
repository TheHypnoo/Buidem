package com.sergigonzalez.buidem.ui.fragments.TypeMachine

import android.R
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
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.FragmentTypeMachinesBinding
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.adapter.TypeMachineAdapter
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.create.CreateTypeMachineFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TypeMachinesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var listTypeMachines: List<TypeMachines> = emptyList()
    private var _binding: FragmentTypeMachinesBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTypeMachinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Type Machines"
        database = MachinesApplication.getDatabase(this@TypeMachinesFragment.requireContext())
        binding.faCreateTypeMachine.setOnClickListener {
            utilWidgets.replaceFragment(
                CreateTypeMachineFragment(),
                requireActivity()
            )
        }
        assignAdapter()
        getAllTypeMachines()
    }

    private fun getAllTypeMachines() {
        database.MachinesApplication().getAllTypeMachines().observe(viewLifecycleOwner) {
            listTypeMachines = it
            binding.rvTypeMachines.addItemDecoration(
                DividerItemDecoration(
                    this@TypeMachinesFragment.context,
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
                        val checkCreatedTypeMachine: Boolean = database.MachinesApplication()
                            .searchTypeMachineinMachine(listTypeMachines[position]._id)
                        withContext(Dispatchers.Main) {
                            if (checkCreatedTypeMachine) {
                                utilWidgets.snackbarMessage(
                                    binding.root,
                                    "Hay una Maquina asignada a este tipo de maquina, debes eliminar primero la maquina",
                                    false
                                )
                                binding.rvTypeMachines.adapter!!.notifyItemChanged(position)
                            } else {
                                val builder = AlertDialog.Builder(view?.context)
                                builder.setMessage("Estas seguro que deseas eliminar la Maquina?\nCon Nombre: ${listTypeMachines[position].nameTypeMachine}\ny Color:  ${listTypeMachines[position].colorTypeMachine}")


                                builder.setPositiveButton(R.string.ok) { _, _ ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        database.MachinesApplication()
                                            .deleteTypeMachine(listTypeMachines[position])
                                    }
                                    binding.rvTypeMachines.adapter!!.notifyItemRemoved(position)
                                }

                                builder.setNegativeButton(R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                    binding.rvTypeMachines.adapter!!.notifyItemChanged(position)
                                }

                                builder.show()
                            }
                        }
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvTypeMachines)

            val adapter = TypeMachineAdapter(listTypeMachines)

            binding.rvTypeMachines.adapter = adapter
            if (listTypeMachines.isEmpty()) {
                binding.tvEmptyTypeMachines.visibility = View.VISIBLE
                binding.rvTypeMachines.visibility = View.GONE
            } else {
                binding.tvEmptyTypeMachines.visibility = View.GONE
                binding.rvTypeMachines.visibility = View.VISIBLE
            }
        }
    }

    private fun assignAdapter() {
        binding.rvTypeMachines.layoutManager =
            LinearLayoutManager(this@TypeMachinesFragment.context)
        val adapter = TypeMachineAdapter(listTypeMachines)
        binding.rvTypeMachines.adapter = adapter
    }

}