package com.sergigonzalez.buidem.ui.fragments.TypeMachine.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.FragmentCreateTypeMachineBinding
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.TypeMachinesFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        binding.fabSaveCreateTypeMachine.setOnClickListener {
            if (binding.etNameTypeMachine.text.isEmpty()) {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún nombre para el tipo de maquina",
                    false
                )
            } else {
                val TypeMachine = TypeMachines(
                    0,
                    binding.etNameTypeMachine.text.toString(),
                    binding.etColorTypeMachine.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    database.MachinesApplication().insertTypeMachine(TypeMachine)
                }
                utilWidgets.replaceFragment(TypeMachinesFragment(), requireActivity())
            }
        }
    }

    fun edit() {
        binding.etNameTypeMachine.setText(typeMachines?.nameTypeMachine)
        binding.etColorTypeMachine.setText(typeMachines?.colorTypeMachine)
        binding.fabSaveCreateTypeMachine.setOnClickListener {
            if (binding.etNameTypeMachine.text.isEmpty()) {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún nombre para el tipo de maquina",
                    false
                )
            } else if (binding.etColorTypeMachine.text.isEmpty()) {
                utilWidgets.snackbarMessage(
                    binding.root,
                    "No has introducido ningún color",
                    false
                )
            } else {
                val typeMachine = TypeMachines(
                    typeMachines!!._id,
                    binding.etNameTypeMachine.text.toString(),
                    binding.etColorTypeMachine.text.toString(),
                )
                CoroutineScope(Dispatchers.IO).launch {
                    database.MachinesApplication().updateTypeMachine(typeMachine)
                }
                utilWidgets.replaceFragment(TypeMachinesFragment(), requireActivity())
            }
        }
    }

    fun openPickerDialog() {
        var defaultColor: Int
        defaultColor = if(typeMachines?.colorTypeMachine?.isNotEmpty() == true) {
            typeMachines!!.colorTypeMachine.toColorInt()
        } else {
            ContextCompat.getColor(requireContext(), R.color.purple_500)
        }
        binding.etColorTypeMachine.setOnClickListener {
            ColorPickerDialog
                .Builder(requireActivity())
                .setTitle("Pick Color")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor(defaultColor)
                .setColorListener { color, colorHex ->
                    defaultColor = color
                    binding.etColorTypeMachine.setText(colorHex)
                }
                .show()
        }
    }
}