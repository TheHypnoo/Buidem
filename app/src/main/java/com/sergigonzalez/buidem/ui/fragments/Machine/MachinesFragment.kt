package com.sergigonzalez.buidem.ui.fragments.Machine

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.FragmentMachinesBinding
import com.sergigonzalez.buidem.ui.fragments.Machine.adapter.MachineAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MachinesFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var listMachines: List<Machines> = emptyList()
    private var _binding: FragmentMachinesBinding? = null
    private val binding get() = _binding!!
    private var singlePosition = 0
    private var orderType = 1

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
            findNavController().navigate(R.id.action_MachineFragment_to_createMachine)
            findNavController().popBackStack(R.id.action_MachineFragment_to_createMachine, true);
        }
        assignAdapter()
        getAllMachines(true)

    }

    private fun getAllMachines(divider: Boolean?) {
        database.MachinesApplication().getAllMachines().observe(viewLifecycleOwner) {
            listMachines = it
            if (divider!!) {
                binding.rvMachines.addItemDecoration(
                    DividerItemDecoration(
                        this@MachinesFragment.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
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

    private fun getSearch(query: String) {
        database.MachinesApplication().searchSerialNumberMachine(query)
            .observe(viewLifecycleOwner) {
                listMachines = it
                val itemTouchCallback = object :
                    ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    ) {
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

    private fun orderAlert() {
        val alert = AlertDialog.Builder(this@MachinesFragment.requireContext())
        alert.setTitle("Order")
        val sorts = arrayOf(
            "Nombre del Cliente (A-Z)",
            "Nombre del Cliente (Z-A)",
            "Zona (A-Z)",
            "Zona (Z-A)",
            "Poblacion (A-Z)",
            "Poblacion (Z-A)",
            "Dirección (A-Z)",
            "Dirección (Z-A)",
            "Fecha de ultima revisión (recientes primero)",
            "Fecha de ultima revisión (antiguas primero)"
        )
        alert.setSingleChoiceItems(
            sorts, singlePosition
        ) { _, which -> singlePosition = which }
        alert.setPositiveButton(
            android.R.string.ok
        ) { _, _ -> selectOrder() }
        alert.setNegativeButton(android.R.string.cancel, null)
        alert.setNeutralButton(
            "Restart"
        ) { _, _ ->
            singlePosition = 10
            selectOrder()
        }
        alert.show()
    }

    private fun selectOrder() {
        when (singlePosition) {
            1 -> orderType = 1
            2 -> orderType = 2
            3 -> orderType = 3
            4 -> orderType = 4
            5 -> orderType = 5
            6 -> orderType = 6
            7 -> orderType = 7
            8 -> orderType = 8
            9 -> orderType = 9
            10 -> orderType = 10
        }
        getOrder()
    }

    private fun getOrder() {
        if (orderType != 10) {
            database.MachinesApplication().getAllMachinesOrder(orderType)
                .observe(viewLifecycleOwner) {
                    listMachines = it

                    val itemTouchCallback = object :
                        ItemTouchHelper.SimpleCallback(
                            0,
                            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                        ) {
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
                                    database.MachinesApplication()
                                        .deleteMachine(listMachines[position])
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
        } else {
            database.MachinesApplication().getAllMachines().observe(viewLifecycleOwner) {
                listMachines = it
                val itemTouchCallback = object :
                    ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    ) {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Ingresa un numero de serie"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    getAllMachines(false)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                getSearch(query)
                return false
            }

        })
    }

    override fun onAttach(context: Context) {
        setHasOptionsMenu(true)
        super.onAttach(context)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_btn_order -> {
                orderAlert()
            }
        }
        return false
    }

}