package com.sergigonzalez.buidem.ui.fragments.Machine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.databinding.ItemMachineBinding

class MachineAdapter(private val listMachines: List<Machines>) :
    RecyclerView.Adapter<MachineAdapter.MachineHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineHolder {
        val viewInflater = LayoutInflater.from(parent.context)
        return MachineHolder(viewInflater.inflate(R.layout.item_machine, parent, false))
    }

    override fun onBindViewHolder(holder: MachineHolder, position: Int) {
        holder.render(listMachines[position])

    }

    override fun getItemCount(): Int = listMachines.size

    class MachineHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMachineBinding.bind(view)

        fun render(Machine: Machines) {
            binding.tvNameClientItem.text = Machine.nameClient
            binding.tvAddressItem.text = Machine.addressMachine
            binding.tvSerialNumberMachineItem.text = Machine.serialNumberMachine
            binding.tvDateLastRevisionItem.text = Machine.lastRevisionDateMachine
        }
    }

}