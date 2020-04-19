package jp.co.tracecovid19.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.hideKeyboard() {
    this.currentFocus?.let {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun AppCompatActivity.showKeyboard(target: View) {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.showSoftInput(target, 0)
}