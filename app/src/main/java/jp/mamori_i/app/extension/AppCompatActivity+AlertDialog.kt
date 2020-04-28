package jp.mamori_i.app.extension

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.start.SplashActivity

fun AppCompatActivity.showErrorDialog(error: MIJError,
                                      retryAction: (() -> Unit)? = null) {
    when(error.action) {
        DialogCloseOnly,DialogRetry,DialogLogout,DialogBack -> {}
        else -> return
    }

    AlertDialog.Builder(this)
        .setMessage(error.message)
        .setPositiveButton("OK") { _, _ ->
            when(error.action) {
                DialogCloseOnly -> {}
                DialogLogout -> {
                    val intent = Intent(this, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.startActivity(intent)
                }
                DialogBack -> {
                    finish()
                }
                DialogRetry -> {
                    retryAction?.invoke()
                }
                else -> {}
            }
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
