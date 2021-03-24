package com.sergigonzalez.buidem.ui.activitys

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.sergigonzalez.buidem.BuildConfig
import com.sergigonzalez.buidem.R
import com.sergigonzalez.buidem.databinding.ActivityLoadingBinding

class Loading : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide();

        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.tvMessage.startAnimation(zoomIn)
        binding.tvVersionApp.text = "Version: ${BuildConfig.VERSION_NAME}"
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        binding.SplashScreenImage.startAnimation(slideAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}