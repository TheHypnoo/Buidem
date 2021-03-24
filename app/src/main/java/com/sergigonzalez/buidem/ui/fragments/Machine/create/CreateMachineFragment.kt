package com.sergigonzalez.buidem.ui.fragments.Machine.create

import android.R
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.FragmentCreateMachineBinding
import com.sergigonzalez.buidem.utils.DialogCalendar
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreateMachineFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentCreateMachineBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: MachinesApplication
    private val c = Calendar.getInstance()
    private var _year = c[Calendar.YEAR]
    private var _month = c[Calendar.MONTH]
    private var _day = c[Calendar.DAY_OF_MONTH]
    private var date: String? = null
    private var listZones: List<Zones> = emptyList()
    private var listTypeMachines: List<TypeMachines> = emptyList()
    private var selectedZone: String = ""
    private var selectedTypeMachine: String = ""
    private var utilWidgets = util_widgets()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMachineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = MachinesApplication.getDatabase(this@CreateMachineFragment.requireContext())
        getZones()
        getTypeMachines()
        _year = c[Calendar.YEAR]
        _month = c[Calendar.MONTH]
        _day = c[Calendar.DAY_OF_MONTH]

        date = if (_month < 10 && _day < 10) {
            "0" + _day + "/" + "0" + (_month + 1) + "/" + _year
        } else if (_month < 10) {
            _day.toString() + "/" + "0" + (_month + 1) + "/" + _year
        } else if (_day < 10) {
            "0" + _day + "/" + (_month + 1) + "/" + _year
        } else {
            _day.toString() + "/" + (_month + 1) + "/" + _year
        }

        binding.etDateLastRevision.text.append(date)
        binding.etDateLastRevision.inputType = InputType.TYPE_NULL
        binding.etDateLastRevision
            .setOnClickListener { v ->
                DialogCalendar.dialog(
                    v.context,
                    binding.etDateLastRevision
                )
            }
        binding.fabSaveCreateMachine.setOnClickListener {
            if (Check()) {
                util_widgets().snackbarMessage(
                    binding.root,
                    "Se ha creado correctamente la Maquina",
                    true
                )
                val machine = Machines(
                    0,
                    binding.etNameClient.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etCodePostal.text.toString(),
                    binding.etTown.text.toString(),
                    binding.etPhoneContact.text.toString(),
                    binding.etEmailContact.text.toString(),
                    binding.etSerialNumberMachine.text.toString(),
                    date!!,
                    selectedZone,
                    selectedTypeMachine
                )
                CoroutineScope(Dispatchers.IO).launch {
                    database.MachinesApplication().insertMachine(machine)
                }
            } else {
                util_widgets().snackbarMessage(
                    binding.root,
                    "No puedes crear una maquina sin introducir nada",
                    false
                )
            }
        }
    }

    fun Check(): Boolean {
        when {
            binding.etNameClient.text.isEmpty() -> {
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un nombre al cliente",
                    false
                )
                return false
            }
            binding.etSerialNumberMachine.text.isEmpty() -> {
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un numero de serie a la maquina",
                    false
                )
                return false
            }
            binding.etCodePostal.text.isEmpty() -> {
                util_widgets().snackbarMessage(binding.root, "Debes añadir un Codigo Postal", false)
                return false
            }
            binding.etTown.text.isEmpty() -> {
                util_widgets().snackbarMessage(binding.root, "Debes añadir una poblacion", false)
                return false
            }
            binding.etAddress.text.isEmpty() -> {
                util_widgets().snackbarMessage(binding.root, "Debes añadir una dirección", false)
                return false
            }
            selectedZone.isEmpty() -> {
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir una zona antes de crear la maquina",
                    false
                )
                return false
            }
            selectedTypeMachine.isEmpty() -> {
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir una zona antes de crear la maquina",
                    false
                )
                return false
            }
            else -> return true
        }
    }

    fun getZones() {
        database.MachinesApplication().getAllZones().observe(viewLifecycleOwner) {
            listZones = it
            binding.spZone.adapter = ArrayAdapter(
                this@CreateMachineFragment.requireContext(),
                R.layout.simple_spinner_item,
                it.map { zones -> zones.nameZone }
            )
            binding.spZone.onItemSelectedListener = this@CreateMachineFragment
        }
    }

    fun getTypeMachines() {
        database.MachinesApplication().getAllTypeMachines().observe(viewLifecycleOwner) {
            listTypeMachines = it
            binding.spTypeMachine.adapter = ArrayAdapter(
                this@CreateMachineFragment.requireContext(),
                R.layout.simple_spinner_item,
                it.map { TypeMachines -> TypeMachines.nameTypeMachine }
            )
            binding.spTypeMachine.onItemSelectedListener = this@CreateMachineFragment
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            if (parent.id == binding.spZone.id) {
                selectedZone = listZones[position]._id.toString()
            } else {
                if (parent.id == binding.spTypeMachine.id) {
                    selectedTypeMachine = listTypeMachines[position]._id.toString()
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}