package com.sergigonzalez.buidem.ui.fragments.Zone.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.ItemZoneBinding
import com.sergigonzalez.buidem.ui.fragments.Maps.MapsFragment
import com.sergigonzalez.buidem.ui.fragments.Zone.create.CreateZonesFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ZoneAdapter(private val listZone: List<Zones>) :
    RecyclerView.Adapter<ZoneAdapter.ZoneHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneHolder {
        val viewInflater = LayoutInflater.from(parent.context)
        return ZoneHolder(viewInflater.inflate(R.layout.item_zone, parent, false))
    }

    override fun onBindViewHolder(holder: ZoneHolder, position: Int) {
        holder.render(listZone[position])

    }

    override fun getItemCount(): Int = listZone.size

    class ZoneHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemZoneBinding.bind(view)
        private var utilWidgets = util_widgets()
        private lateinit var database: MachinesApplication
        private var listMachines = ArrayList<Machines>()

        fun render(Zones: Zones) {
            database = MachinesApplication.getDatabase(view.context)

            binding.root.setOnClickListener {
                val bt = BottomSheetDialog(binding.root.context, R.style.BottomSheetTheme)
                val view: View = LayoutInflater.from(view.context)
                    .inflate(R.layout.bottom_sheet_zones, null)

                view.findViewById<View>(R.id.llGoogleMaps).setOnClickListener {
                    val activity = it.context as? AppCompatActivity
                    CoroutineScope(Dispatchers.IO).launch {
                        listMachines = database.MachinesApplication().searchMachinesbyIDZone(Zones._id) as ArrayList<Machines>
                    }.invokeOnCompletion {
                        if (activity != null) {
                            val mapsFragment = MapsFragment()
                            val bundle = Bundle()
                            bundle.putSerializable("listMachines", listMachines)
                            mapsFragment.arguments = bundle
                            utilWidgets.replaceFragment(mapsFragment, activity)
                            bt.dismiss()
                            //Debo cambiar el target del Bottom Navigation Bar
                        }
                    }
                    bt.dismiss()
                }
                view.findViewById<View>(R.id.llEdit).setOnClickListener {
                    val activity  = it.context as? AppCompatActivity
                    if (activity != null) {
                        val createZonesFragment = CreateZonesFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("Zone", Zones)
                        createZonesFragment.arguments = bundle
                        utilWidgets.replaceFragment(createZonesFragment, activity)
                    }
                    bt.dismiss()
                }
                bt.setContentView(view)
                bt.show()
            }
            binding.cvZone.animation = AnimationUtils.loadAnimation(view.context,R.anim.scale_up)
            binding.tvNameZoneItem.text = Zones.nameZone
        }
    }

}