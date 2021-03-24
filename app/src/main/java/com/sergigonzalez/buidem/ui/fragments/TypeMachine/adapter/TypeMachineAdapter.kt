package com.sergigonzalez.buidem.ui.fragments.TypeMachine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.TypeMachines
import com.sergigonzalez.buidem.databinding.ItemMachineBinding
import com.sergigonzalez.buidem.databinding.ItemTypemachineBinding

class TypeMachineAdapter(private val listTypeMachine: List<TypeMachines>) :
    RecyclerView.Adapter<TypeMachineAdapter.MachineHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineHolder {
        val viewInflater = LayoutInflater.from(parent.context)
        return MachineHolder(viewInflater.inflate(R.layout.item_typemachine, parent, false))
    }

    override fun onBindViewHolder(holder: MachineHolder, position: Int) {
        holder.render(listTypeMachine[position])

    }

    override fun getItemCount(): Int = listTypeMachine.size

    class MachineHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTypemachineBinding.bind(view)

        fun render(TypeMachine: TypeMachines) {
            binding.cvTypeMachine.animation = AnimationUtils.loadAnimation(view.context,R.anim.scale_up)
            binding.tvTypeMachineItem.text = TypeMachine.nameTypeMachine
            binding.tvColorTypeMachine.text = TypeMachine.colorTypeMachine
        }
    }

}