package com.sergigonzalez.buidem.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.sergigonzalez.buidem.R


class util_widgets {

    fun replaceFragment(fragment: Fragment, activity: FragmentActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun snackbarMessage(_view: View, _message: String, CorrectorIncorrect: Boolean) {
        if (CorrectorIncorrect) {
            val snackbar = Snackbar.make(_view, _message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#ff669900"))
            val textView = snackbar.view.findViewById(R.id.snackbar_action) as TextView
            val imgClose = ImageView(_view.context)
            imgClose.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imgClose.startAnimation(AnimationUtils.loadAnimation(_view.context, R.anim.zoom_in))
            val layImageParams = ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            imgClose.setImageResource(R.drawable.ic_check)
            (textView.parent as SnackbarContentLayout).addView(imgClose, layImageParams)
            imgClose.setOnClickListener { snackbar.dismiss() }
            snackbar.show()
        } else {
            val snackbar = Snackbar.make(_view, _message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#B00020"))
            val textView = snackbar.view.findViewById(R.id.snackbar_action) as TextView
            val imgClose = ImageView(_view.context)
            imgClose.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imgClose.startAnimation(AnimationUtils.loadAnimation(_view.context, R.anim.zoom_in))
            val layImageParams = ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            imgClose.setImageResource(R.drawable.ic_close)
            (textView.parent as SnackbarContentLayout).addView(imgClose, layImageParams)
            imgClose.setOnClickListener { snackbar.dismiss() }
            snackbar.show()
        }
    }

    fun convertFahreheit(x: Double): Double {
        return x - 273.15
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    object hideKeyboard {

        fun hideSoftKeyBoard(context: Context, view: View) {
            try {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}


