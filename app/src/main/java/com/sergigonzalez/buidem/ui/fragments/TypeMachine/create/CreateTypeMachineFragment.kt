package com.sergigonzalez.buidem.ui.fragments.TypeMachine.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.FragmentCreateTypeMachineBinding
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateTypeMachineFragment : Fragment() {
    private lateinit var database: MachinesApplication
    private var _binding: FragmentCreateTypeMachineBinding? = null
    private val binding get() = _binding!!
    private var utilWidgets = util_widgets()

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
            }
        }
    }
}