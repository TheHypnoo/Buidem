package com.sergigonzalez.buidem.ui.fragments.Machine.create


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.sergigonzalez.buidem.R
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
import kotlinx.coroutines.withContext
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
    private var selectedZone: Int = 0
    private var selectedTypeMachine: Int = 0
    private var createdZone: Boolean = false
    private var createdTypeMachine: Boolean = false
    private var machine: Machines? = null
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
        date()
        getZones()
        getTypeMachines()
        if (machine != null) {
            edit()
        } else {
            create()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        machine = arguments?.getSerializable("Machine") as Machines?
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            if (parent.id == binding.spZone.id) {
                if (listZones.isNotEmpty()) selectedZone =
                    listZones[position]._id
            } else {
                if (parent.id == binding.spTypeMachine.id) {
                    if (listTypeMachines.isNotEmpty()) selectedTypeMachine =
                        listTypeMachines[position]._id
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun date() {
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

        binding.etDateLastRevision.text?.append(date)
        binding.etDateLastRevision.inputType = InputType.TYPE_NULL
        binding.etDateLastRevision
            .setOnClickListener { v ->
                DialogCalendar.dialog(
                    v.context,
                    binding.etDateLastRevision
                )
            }
    }

    private fun create() {
        binding.btnCreateMachine.setOnClickListener {
            if (Check()) {
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
                    selectedTypeMachine,
                    selectedZone
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().insertMachine(machine)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createMachine_to_MachineFragment) }
                    } catch (e: SQLiteConstraintException) {
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                        util_widgets().snackbarMessage(
                            binding.root,
                            "Ya existe el numero de serie",
                            false
                        )
                    }
                }
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "Se ha creado correctamente la Maquina",
                    true
                )
            }
        }
    }

    private fun Check(): Boolean {
        when {
            binding.etNameClient.text?.isEmpty() == true -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un nombre al cliente",
                    false
                )
                return false
            }
            binding.etSerialNumberMachine.text?.isEmpty() == true -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un numero de serie a la maquina",
                    false
                )
                return false
            }
            binding.etCodePostal.text?.isEmpty() == true -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(binding.root, "Debes añadir un Codigo Postal", false)
                return false
            }
            binding.etTown.text?.isEmpty() == true -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(binding.root, "Debes añadir una poblacion", false)
                return false
            }
            binding.etAddress.text?.isEmpty() == true -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(binding.root, "Debes añadir una dirección", false)
                return false
            }
            selectedZone == 0 -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir una zona antes de crear la maquina",
                    false
                )
                return false
            }
            selectedTypeMachine == 0 -> {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
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

    private fun getZones() {
        var nameZone = ""
        database.MachinesApplication().getAllZones().observe(viewLifecycleOwner) {
            listZones = it
            if (it.isNotEmpty()) {
                binding.spZone.adapter = ArrayAdapter(
                    this@CreateMachineFragment.requireContext(),
                    android.R.layout.simple_spinner_item,
                    it.map { zones -> zones.nameZone }
                )
                if (machine != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        nameZone = database.MachinesApplication()
                            .searchZonebyID(machine?.zone!!).nameZone
                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (!createdZone) {
                                for (d in listZones.indices) {
                                    if (listZones[d].nameZone == nameZone) {
                                        binding.spZone.setSelection(d)
                                    }
                                }
                            } else if (createdZone) {
                                selectedZone = listZones[listZones.size - 1]._id
                                binding.spZone.setSelection(listZones.size - 1)
                            }
                        }
                    }
                } else if (createdZone) {
                    selectedZone = listZones[listZones.size - 1]._id
                    binding.spZone.setSelection(listZones.size - 1)
                }
            }
        }
        binding.spZone.onItemSelectedListener = this@CreateMachineFragment
        binding.ivCreateZone.setOnClickListener {
            createZoneifEmpty()
        }
        if (createdZone) {
            selectedZone = listZones[listZones.size - 1]._id
            binding.spZone.setSelection(listZones.size - 1)
        }
    }

    private fun createZoneifEmpty() {
        val alertZone = AlertDialog.Builder(this@CreateMachineFragment.context)
        val inflater = this.layoutInflater
        val v2: View = inflater.inflate(R.layout.fragment_create_zones, null)
        val etNomZona = v2.findViewById<EditText>(R.id.editTextZone)
        val btnCreateZone = v2.findViewById<Button>(R.id.btn_create_zone)
        btnCreateZone.visibility = View.GONE
        alertZone.setView(v2).setPositiveButton("Create Zone") { dialog, which ->
            val nomZona = etNomZona.text.toString()
            if (nomZona.isNotEmpty()) {
                val zone = Zones(
                    0,
                    nomZona,
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().insertZones(zone)
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                        withContext(Dispatchers.Main) {
                            createdZone = true
                            getZones()
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
            } else {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningun nombre para la zona",
                    false
                )
            }
        }
        alertZone.setNegativeButton("Cancel", null)
        val dialog = alertZone.create()
        dialog.show()
    }

    @SuppressLint("ResourceType")
    private fun createTypeMachineifEmpty() {
        val alertTypeMachine = AlertDialog.Builder(this@CreateMachineFragment.context)
        val inflater = this.layoutInflater
        val v2: View = inflater.inflate(R.layout.fragment_create_type_machine, null)
        val etNomTypeMachine = v2.findViewById<EditText>(R.id.editTextTypeMachine)
        val btnCreateTypeMachine = v2.findViewById<Button>(R.id.btn_create_typeMachine)
        btnCreateTypeMachine.visibility = View.GONE
        val etColor = v2.findViewById<TextView>(R.id.color_background)
        val ivColor = v2.findViewById<ImageView>(R.id.select_color)
        etColor.text = "#6200ee"
        etColor.setBackgroundColor("#6200ee".toColorInt())
        var colorDefault = ContextCompat.getColor(requireContext(), R.color.purple_500)
        ivColor.setOnClickListener {
            ColorPickerDialog
                .Builder(requireActivity())
                .setTitle("Pick Color")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor(colorDefault)
                .setColorListener { color, colorHex ->
                    colorDefault = color
                    etColor.setBackgroundColor(color)
                    etColor.text = colorHex
                }
                .show()
        }
        alertTypeMachine.setView(v2).setPositiveButton("Create Type Machine") { dialog, which ->
            val nomTypeMachine = etNomTypeMachine.text.toString()
            if (nomTypeMachine.isNotEmpty()) {
                val typeMachine = TypeMachines(
                    0,
                    nomTypeMachine,
                    etColor.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().insertTypeMachine(typeMachine)
                        withContext(Dispatchers.Main) {
                            createdTypeMachine = true
                            getTypeMachines()
                        }
                    } catch (e: SQLiteConstraintException) {
                        util_widgets.hideKeyboard
                        utilWidgets.snackbarMessage(
                            binding.root,
                            "Ya existe el nombre de ese tipo de maquina",
                            false
                        )
                    }
                }
            } else {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningun nombre para el Type Machine",
                    false
                )
            }
        }
        alertTypeMachine.setNegativeButton("Cancel", null)
        val dialog = alertTypeMachine.create()
        dialog.show()
    }

    private fun getTypeMachines() {
        var nameTypeMachine = ""
        database.MachinesApplication().getAllTypeMachines().observe(viewLifecycleOwner) {
            listTypeMachines = it
            if (it.isNotEmpty()) {
                binding.spTypeMachine.adapter = ArrayAdapter(
                    this@CreateMachineFragment.requireContext(),
                    android.R.layout.simple_spinner_item,
                    it.map { TypeMachines -> TypeMachines.nameTypeMachine }
                )
                if (machine != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        nameTypeMachine = database.MachinesApplication()
                            .searchTypeMachinebyID(machine?.typeMachine!!).nameTypeMachine
                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (!createdTypeMachine) {
                                for (d in listTypeMachines.indices) {
                                    if (listTypeMachines[d].nameTypeMachine == nameTypeMachine) {
                                        binding.spTypeMachine.setSelection(d)
                                    }
                                }
                            } else if(createdTypeMachine) {
                                selectedTypeMachine = listTypeMachines[listTypeMachines.size - 1]._id
                                binding.spTypeMachine.setSelection(listTypeMachines.size - 1)
                            }
                        }
                    }
                } else if(createdTypeMachine) {
                    selectedTypeMachine = listTypeMachines[listTypeMachines.size - 1]._id
                    binding.spTypeMachine.setSelection(listTypeMachines.size - 1)
                }
            }
            binding.spTypeMachine.onItemSelectedListener = this@CreateMachineFragment
            binding.ivCreateTypeMachine.setOnClickListener {
                createTypeMachineifEmpty()
            }
        }
        if (createdTypeMachine) {
            selectedTypeMachine = listTypeMachines[listTypeMachines.size - 1]._id
            binding.spTypeMachine.setSelection(listTypeMachines.size - 1)
        }
    }

    private fun edit() {
        binding.btnCreateMachine.text = "Edit Machine"
        binding.etNameClient.setText(machine?.nameClient)
        binding.etSerialNumberMachine.setText(machine?.serialNumberMachine)
        binding.etPhoneContact.setText(machine?.phoneContact)
        binding.etEmailContact.setText(machine?.emailContact)
        binding.etDateLastRevision.setText(machine?.lastRevisionDateMachine)
        binding.etCodePostal.setText(machine?.postalCodeMachine)
        binding.etTown.setText(machine?.townMachine)
        binding.etAddress.setText(machine?.addressMachine)

        binding.btnCreateMachine.setOnClickListener {
            if (Check()) {
                val machine = Machines(
                    machine!!._id,
                    binding.etNameClient.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etCodePostal.text.toString(),
                    binding.etTown.text.toString(),
                    binding.etPhoneContact.text.toString(),
                    binding.etEmailContact.text.toString(),
                    binding.etSerialNumberMachine.text.toString(),
                    date!!,
                    selectedTypeMachine,
                    selectedZone
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().updateMachine(machine)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createMachine_to_MachineFragment) }
                    } catch (e: SQLiteConstraintException) {
                        activity?.let { it1 ->
                            util_widgets.hideKeyboard.hideSoftKeyBoard(
                                it1.applicationContext,
                                binding.root
                            )
                        }
                        util_widgets().snackbarMessage(
                            binding.root,
                            "Ya existe el numero de serie",
                            false
                        )
                    }
                }
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "Se ha Actualizado correctamente la Maquina",
                    true
                )
            } else {
                activity?.let { it1 ->
                    util_widgets.hideKeyboard.hideSoftKeyBoard(
                        it1.applicationContext,
                        binding.root
                    )
                }
                util_widgets().snackbarMessage(
                    binding.root,
                    "No puedes actualizar una maquina sin introducir nada",
                    false
                )
            }
        }
    }

}