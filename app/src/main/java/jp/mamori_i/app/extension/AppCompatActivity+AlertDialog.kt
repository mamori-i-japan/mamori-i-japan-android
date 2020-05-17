package jp.mamori_i.app.extension

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.showAlertDialog(title: String,
                                      message: String,
                                      completion: (() -> Unit)? = null) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { _, _ ->
            completion?.invoke()
        }
        .setCancelable(completion == null)
        .show()
}

fun AppCompatActivity.showConfirmAlertDialog(title: String,
                                             message: String,
                                             completion: (() -> Unit)? = null) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { _, _ ->
            completion?.invoke()
        }
        .setNegativeButton("キャンセル") { _, _ ->
        }
        .show()
}
