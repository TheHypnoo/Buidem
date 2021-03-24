package com.sergigonzalez.buidem.ui.fragments.Zone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Zones
import com.sergigonzalez.buidem.databinding.ItemZoneBinding

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

        fun render(Zones: Zones) {
            binding.cvZone.animation = AnimationUtils.loadAnimation(view.context,R.anim.scale_up)
            binding.tvNameZoneItem.text = Zones.nameZone
        }
    }

}