package com.sergigonzalez.buidem.ui.fragments.TypeMachine.create

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.FragmentCreateTypeMachineBinding
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateTypeMachineFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var _binding: FragmentCreateTypeMachineBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()
    private var typeMachines: TypeMachines? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTypeMachineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = MachinesApplication.getDatabase(this@CreateTypeMachineFragment.requireContext())
        if (typeMachines != null) {
            edit()
        } else {
            create()
        }
        openPickerDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        typeMachines = arguments?.getSerializable("TypeMachine") as TypeMachines?
    }

    fun create() {
        binding.colorBackground.text = "#6200ee"
        binding.colorBackground.setBackgroundColor("#6200ee".toColorInt())
        binding.btnCreateTypeMachine.setOnClickListener {
            if (binding.editTextTypeMachine.text?.isEmpty() == true) {
                hideKeyboard()
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún nombre para el tipo de maquina",
                    false
                )
            } else {
                val typeMachine = TypeMachines(
                    0,
                    binding.editTextTypeMachine.text.toString(),
                    binding.colorBackground.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().insertTypeMachine(typeMachine)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createTypeMachine_to_TypeMachineFragment) }
                    } catch (e: SQLiteConstraintException) {
                        hideKeyboard()
                        utilWidgets.snackbarMessage(
                            binding.root,
                            "Ya existe el nombre de ese tipo de maquina",
                            false
                        )
                    }
                }
            }
        }
    }

    fun edit() {
        binding.editTextTypeMachine.setText(typeMachines?.nameTypeMachine)
        if (!typeMachines?.colorTypeMachine.isNullOrEmpty()) {
            binding.colorBackground.setBackgroundColor(typeMachines?.colorTypeMachine!!.toColorInt())
        }
        binding.colorBackground.text = typeMachines!!.colorTypeMachine
        binding.btnCreateTypeMachine.setOnClickListener {
            if (binding.editTextTypeMachine.text?.isEmpty() == true) {
                hideKeyboard()
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún nombre para el tipo de maquina",
                    false
                )
            } else {
                val typeMachine = TypeMachines(
                    typeMachines!!._id,
                    binding.editTextTypeMachine.text.toString(),
                    binding.colorBackground.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        database.MachinesApplication().updateTypeMachine(typeMachine)
                        withContext(Dispatchers.Main) { findNavController().navigate(R.id.action_createTypeMachine_to_TypeMachineFragment) }
                    } catch (e: SQLiteConstraintException) {
                        hideKeyboard()
                        utilWidgets.snackbarMessage(
                            binding.root,
                            "Ya existe el nombre de ese tipo de maquina",
                            true
                        )
                    }
                }
            }
        }
    }

    fun openPickerDialog() {
        var defaultColor: Int
        defaultColor = if (typeMachines?.colorTypeMachine?.isNotEmpty() == true) {
            typeMachines!!.colorTypeMachine.toColorInt()
        } else {
            ContextCompat.getColor(requireContext(), R.color.purple_500)
        }
        binding.selectColor.setOnClickListener {
            ColorPickerDialog
                .Builder(requireActivity())
                .setTitle("Pick Color")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor(defaultColor)
                .setColorListener { color, colorHex ->
                    defaultColor = color
                    binding.colorBackground.setBackgroundColor(color)
                    binding.colorBackground.text = colorHex
                }
                .show()
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