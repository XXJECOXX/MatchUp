package com.epyco.matchup.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.Normalizer
import java.util.*

object Utilities {

    fun hideKeyboard(activity: Activity, view: View? = null) {
        val viewWithFocus = activity.currentFocus
        val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (viewWithFocus != null) {
            inputManager.hideSoftInputFromWindow(viewWithFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } else if (view != null) {
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    fun required(field: EditText): Boolean {
        var valid = true
        if (field.text.toString().isBlank()) {
            field.text.clear()
            field.error = "required"
            field.setHintTextColor(Color.RED)
            field.requestFocus()
            valid = false
            field.isFocusable = true
        }
        return valid
    }

    fun required(fields: Array<EditText>): Boolean {
        var valid = true
        for (field in fields) {
            valid = required(field) && valid
        }
        return valid
    }

    fun normalizer(str: String): String {
        return Normalizer.normalize(str.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}