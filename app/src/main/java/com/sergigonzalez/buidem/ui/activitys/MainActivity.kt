package com.sergigonzalez.buidem.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.databinding.ActivityMainBinding
import com.sergigonzalez.buidem.ui.fragments.*
import com.sergigonzalez.buidem.utils.util_widgets


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var utilWidgets = util_widgets()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.MachineFragment,
                R.id.ZoneFragment,
                R.id.TypeMachineFragment,
                R.id.MapsFragment,
            )
        )
        val navController = Navigation.findNavController(this, R.id.fragment_container)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(binding.bottomNavigation, navController)


        //val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //val navController = findNavController(R.id.fragment_container)


       /* navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.loginFragment -> hideBottomNav()
                R.id.registerFragment -> hideBottomNav()
                R.id.viewPagerFragment -> hideBottomNav()
                R.id.splashFragment -> hideBottomNav()
                R.id.settingsFragment -> hideBottomNav()
                R.id.detailsFragment -> hideBottomNav()
                R.id.homeFragment -> showBottomNav()
                R.id.sneakersFragment -> showBottomNav()
                R.id.profileFragment -> showBottomNav()
            }

        }*/

        //bottomNavigationView.setupWithNavController(navController)
        /*binding.bottomNavigationView.background = null
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
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this,
            R.id.fragment_container
        ).navigateUp() || super.onSupportNavigateUp()
    }


}