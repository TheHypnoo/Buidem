package com.sergigonzalez.buidem.ui.fragments.TypeMachine.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.ItemTypemachineBinding
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.create.CreateTypeMachineFragment
import com.sergigonzalez.buidem.utils.util_widgets

class TypeMachineAdapter(private val listTypeMachine: List<TypeMachines>) :
    RecyclerView.Adapter<TypeMachineAdapter.TypeMachineHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeMachineHolder {
        val viewInflater = LayoutInflater.from(parent.context)
        return TypeMachineHolder(viewInflater.inflate(R.layout.item_typemachine, parent, false))
    }

    override fun onBindViewHolder(holder: TypeMachineHolder, position: Int) {
        holder.render(listTypeMachine[position])

    }

    override fun getItemCount(): Int = listTypeMachine.size

    class TypeMachineHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTypemachineBinding.bind(view)
        private var utilWidgets = util_widgets()

        fun render(TypeMachine: TypeMachines) {
            binding.root.setOnClickListener {
                val bt = BottomSheetDialog(binding.root.context, R.style.BottomSheetTheme)
                val view: View = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.bottom_sheet_typemachine, null)

                view.findViewById<View>(R.id.llEdit).setOnClickListener {
                    val activity = it.context as? AppCompatActivity
                    if (activity != null) {
                        val createTypeMachineFragment = CreateTypeMachineFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("TypeMachine", TypeMachine)
                        createTypeMachineFragment.arguments = bundle
                        utilWidgets.replaceFragment(createTypeMachineFragment, activity)
                    }
                    bt.dismiss()
                }
                bt.setContentView(view)
                bt.show()
            }

            binding.cvTypeMachine.animation =
                AnimationUtils.loadAnimation(view.context, R.anim.scale_up)
            binding.tvTypeMachineItem.text = TypeMachine.nameTypeMachine
            if (TypeMachine.colorTypeMachine.isNotEmpty()) {
                binding.tvColorTypeMachine.text = TypeMachine.colorTypeMachine
                binding.tvColorTypeMachine.setTextColor(Color.parseColor(TypeMachine.colorTypeMachine))
            } else {
                binding.tvColorTypeMachine.text = "No Color"
            }
        }
    }

}