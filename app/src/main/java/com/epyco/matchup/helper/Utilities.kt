package com.epyco.matchup.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.epyco.matchup.R
import java.text.Normalizer
import java.text.NumberFormat
import java.util.*

object Utilities {
    var animationButtonClick = AlphaAnimation(1f, 0.3f)

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            valid = required(field)
        }
        return valid
    }


    fun currencyFormat(value: Double): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 0
        return format.format(value)
    }

    fun cleanJSONResponse(response: String): String {
        return response.replace("\"null\"", "null").replace("null", "\"\"")
    }

    fun customToast(context: Context?, resource: Int) {
        val layout = View.inflate(context, R.layout.custom_toast, null)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.setText(resource)
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    fun normalizer(str: String): String {
        return Normalizer.normalize(str.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}