package com.sergigonzalez.buidem.utils

import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.sergigonzalez.buidem.R

class util_widgets {

    fun replaceFragment(fragment: Fragment, activity: FragmentActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    fun snackbarMessage(_view: View, _message: String, CorrectorIncorrect: Boolean) {
        if (CorrectorIncorrect) Snackbar.make(_view, _message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor("#ff669900")).show()
        else
            Snackbar.make(_view, _message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#B00020")).show()
    }
}