package jp.co.tracecovid19.extension

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jp.co.tracecovid19.screen.common.TraceCovid19Error

fun AppCompatActivity.showErrorAlert(error: TraceCovid19Error,
                                     completion: (() -> Unit)? = null) {
    // TODO 文言を切り出し
    AlertDialog.Builder(this)
        .setMessage(error.message)
        .setPositiveButton("OK") { _, _ ->
            completion?.invoke()
        }
        .show()
}

fun AppCompatActivity.showSimpleMessageAlert(message: String,
                                             completion: (() -> Unit)? = null) {
    // TODO 文言を切り出し
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton("OK") { _, _ ->
            completion?.invoke()
        }
        .show()
}

fun AppCompatActivity.showConfirmMessageAlert(message: String,
                                              positiveButtonTitle: String,
                                              completion: (() -> Unit)? = null) {
    // TODO 文言を切り出し
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(positiveButtonTitle) { _, _ ->
            completion?.invoke()
        }
        .setNegativeButton("キャンセル") { _, _ ->

        }
        .show()
}