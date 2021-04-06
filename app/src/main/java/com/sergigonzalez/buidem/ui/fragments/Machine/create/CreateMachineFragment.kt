package com.sergigonzalez.buidem.ui.fragments.Machine.create


import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.FragmentCreateMachineBinding
import com.sergigonzalez.buidem.ui.fragments.Machine.MachinesFragment
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
    private var selectedZone: Int = 0
    private var selectedTypeMachine: Int = 0
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

    fun date() {
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
    }

    fun create() {
        binding.fabSaveCreateMachine.setOnClickListener {
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
                        utilWidgets.replaceFragment(MachinesFragment(), requireActivity())
                    } catch (e: SQLiteConstraintException) {
                        hideKeyboard()
                        util_widgets().snackbarMessage(
                            binding.root,
                            "Ya existe el numero de serie",
                            false
                        )
                    }
                }
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "Se ha creado correctamente la Maquina",
                    true
                )
            }
        }
    }

    fun Check(): Boolean {
        when {
            binding.etNameClient.text.isEmpty() -> {
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un nombre al cliente",
                    false
                )
                return false
            }
            binding.etSerialNumberMachine.text.isEmpty() -> {
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir un numero de serie a la maquina",
                    false
                )
                return false
            }
            binding.etCodePostal.text.isEmpty() -> {
                hideKeyboard()
                util_widgets().snackbarMessage(binding.root, "Debes añadir un Codigo Postal", false)
                return false
            }
            binding.etTown.text.isEmpty() -> {
                hideKeyboard()
                util_widgets().snackbarMessage(binding.root, "Debes añadir una poblacion", false)
                return false
            }
            binding.etAddress.text.isEmpty() -> {
                hideKeyboard()
                util_widgets().snackbarMessage(binding.root, "Debes añadir una dirección", false)
                return false
            }
            selectedZone == 0 -> {
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "Debes añadir una zona antes de crear la maquina",
                    false
                )
                return false
            }
            selectedTypeMachine == 0 -> {
                hideKeyboard()
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
            if (it.isNotEmpty()) {
                binding.spZone.adapter = ArrayAdapter(
                    this@CreateMachineFragment.requireContext(),
                    android.R.layout.simple_spinner_item,
                    it.map { zones -> zones.nameZone }
                )
                binding.spZone.onItemSelectedListener = this@CreateMachineFragment
                if (machine != null) {
                    binding.spZone.setSelection(machine?.zone!! - 1)
                }
            }/* else {
                var lista: List<String> = listOf("No hay ningúna zona")
                binding.spZone.adapter = ArrayAdapter(
                    this@CreateMachineFragment.requireContext(),
                    android.R.layout.simple_spinner_item,
                    lista
                )
                binding.spZone.onItemSelectedListener = this@CreateMachineFragment
                if (machine != null) {
                    binding.spZone.setSelection(machine?.zone!! - 1)
                }
            }*/

        }
    }

    fun getTypeMachines() {
        database.MachinesApplication().getAllTypeMachines().observe(viewLifecycleOwner) {
            listTypeMachines = it
            binding.spTypeMachine.adapter = ArrayAdapter(
                this@CreateMachineFragment.requireContext(),
                android.R.layout.simple_spinner_item,
                it.map { TypeMachines -> TypeMachines.nameTypeMachine }
            )
            binding.spTypeMachine.onItemSelectedListener = this@CreateMachineFragment
            if (machine != null) {
                binding.spTypeMachine.setSelection(machine?.typeMachine!! - 1)
            }
        }
    }

    fun edit() {
        binding.etNameClient.setText(machine?.nameClient)
        binding.etSerialNumberMachine.setText(machine?.serialNumberMachine)
        binding.etPhoneContact.setText(machine?.phoneContact)
        binding.etEmailContact.setText(machine?.emailContact)
        binding.etDateLastRevision.setText(machine?.lastRevisionDateMachine)
        binding.etCodePostal.setText(machine?.postalCodeMachine)
        binding.etTown.setText(machine?.townMachine)
        binding.etAddress.setText(machine?.addressMachine)

        binding.fabSaveCreateMachine.setOnClickListener {
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
                        utilWidgets.replaceFragment(MachinesFragment(), requireActivity())
                    } catch (e: SQLiteConstraintException) {
                        hideKeyboard()
                        util_widgets().snackbarMessage(
                            binding.root,
                            "Ya existe el numero de serie",
                            false
                        )
                    }
                }
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "Se ha Actualizado correctamente la Maquina",
                    true
                )
            } else {
                hideKeyboard()
                util_widgets().snackbarMessage(
                    binding.root,
                    "No puedes actualizar una maquina sin introducir nada",
                    false
                )
            }
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}