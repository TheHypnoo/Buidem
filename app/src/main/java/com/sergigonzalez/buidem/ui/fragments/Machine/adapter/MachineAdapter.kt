package com.sergigonzalez.buidem.ui.fragments.Machine.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.databinding.ItemMachineBinding


class MachineAdapter(private val listMachines: List<Machines>) :
    RecyclerView.Adapter<MachineAdapter.MachineHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineHolder {
        val viewInflater = LayoutInflater.from(parent.context)
        return MachineHolder(viewInflater.inflate(R.layout.item_machine, parent, false))
    }

    override fun onBindViewHolder(holder: MachineHolder, position: Int) =
        holder.render(listMachines[position])

    override fun getItemCount(): Int = listMachines.size

    class MachineHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMachineBinding.bind(view)

        fun render(Machine: Machines) {
            binding.root.setOnClickListener {
                val bt = BottomSheetDialog(binding.root.context, R.style.BottomSheetTheme)
                val view: View = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.bottom_sheet_main, null)
                view.findViewById<View>(R.id.llCall).setOnClickListener {
                    binding.root.context.startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel: ${Machine.phoneContact}")
                        )
                    )

                    bt.dismiss()
                }
                view.findViewById<View>(R.id.llEmail).setOnClickListener {
                    val sendIntent = Intent(Intent.ACTION_SENDTO)
                    val uriText =
                        ("mailto:" + Machine.emailContact
                                + "?subject=" +
                                Uri.encode(
                                    "Propera revisió màquina nº "
                                            + Machine.serialNumberMachine
                                ))
                    sendIntent.data = Uri.parse(uriText)
                    binding.root.context.startActivity(
                        Intent.createChooser(
                            sendIntent,
                            "Enviar email"
                        )
                    )
                    bt.dismiss()
                }
                view.findViewById<View>(R.id.llGoogleMaps).setOnClickListener {
                    val activity = it.context as? AppCompatActivity
                    if (activity != null) {
                        val bundle = Bundle()
                        bundle.putSerializable("Machine", Machine)
                        val navController =
                            Navigation.findNavController(activity, R.id.fragment_container)
                        navController.navigate(R.id.action_MachineFragment_to_MapsFragment, bundle)
                    }
                    bt.dismiss()
                }
                view.findViewById<View>(R.id.llEdit).setOnClickListener {
                    val activity = it.context as? AppCompatActivity
                    if (activity != null) {
                        val bundle = Bundle()
                        bundle.putSerializable("Machine", Machine)
                        val navController =
                            Navigation.findNavController(activity, R.id.fragment_container)
                        navController.navigate(R.id.action_MachineFragment_to_createMachine, bundle)
                    }
                    bt.dismiss()
                }
                bt.setContentView(view)
                bt.show()
            }


            binding.cvMachine.animation = AnimationUtils.loadAnimation(
                view.context,
                R.anim.scale_up
            )
            binding.tvNameClientItem.text = Machine.nameClient
            binding.tvAddressItem.text = Machine.addressMachine
            binding.tvSerialNumberMachineItem.text = Machine.serialNumberMachine
            binding.tvDateLastRevisionItem.text = Machine.lastRevisionDateMachine
        }


    }

}