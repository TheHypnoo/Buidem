package com.sergigonzalez.buidem.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.data.MachinesApplication
import com.sergigonzalez.buidem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: MachinesApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = MachinesApplication.getDatabase(this)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.selectedItemId = R.id.miHome
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> {

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miMovements -> {

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miWeather -> {

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.miInfo -> {

                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}