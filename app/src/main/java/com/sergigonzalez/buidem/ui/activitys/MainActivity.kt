package com.sergigonzalez.buidem.ui.activitys

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.ActivityMainBinding
import com.sergigonzalez.buidem.ui.fragments.*
import com.sergigonzalez.buidem.ui.fragments.Machine.MachinesFragment
import com.sergigonzalez.buidem.ui.fragments.Maps.MapsFragment
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.TypeMachinesFragment
import com.sergigonzalez.buidem.ui.fragments.Zone.ZonesFragment
import com.sergigonzalez.buidem.utils.util_widgets

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: MachinesApplication
    private var utilWidgets = util_widgets()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = MachinesApplication.getDatabase(this)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.selectedItemId = R.id.miHome
        utilWidgets.replaceFragment(MachinesFragment(), this)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> {
                    utilWidgets.replaceFragment(MachinesFragment(), this)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miZones -> {
                    utilWidgets.replaceFragment(ZonesFragment(), this)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miTypeMachines -> {
                    utilWidgets.replaceFragment(TypeMachinesFragment(), this)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miMaps -> {
                    utilWidgets.replaceFragment(MapsFragment(), this)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

}