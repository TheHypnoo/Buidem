package com.sergigonzalez.buidem.ui.activitys

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.Machines
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.ActivityMainBinding
import com.sergigonzalez.buidem.ui.fragments.*
import com.sergigonzalez.buidem.ui.fragments.Machine.MachinesFragment
import com.sergigonzalez.buidem.ui.fragments.Maps.MapsFragment
import com.sergigonzalez.buidem.ui.fragments.TypeMachine.TypeMachinesFragment
import com.sergigonzalez.buidem.ui.fragments.Zone.ZonesFragment
import com.sergigonzalez.buidem.utils.util_widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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