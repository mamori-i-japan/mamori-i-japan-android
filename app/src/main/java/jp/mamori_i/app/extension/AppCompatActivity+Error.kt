package jp.mamori_i.app.extension

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.start.SplashActivity

fun AppCompatActivity.handleError(error: MIJError) {
    when(error.action) {
        DialogCloseOnly,
        DialogRetry,
        DialogLogout,
        DialogBack -> {
            showErrorDialog(error)
        }
        Inline,
        InView -> return
        ForceLogout -> {
            error.logoutAction?.invoke()
            goToSplash()
        }
        ForceScreenBack -> {
            finish()
        }
        else -> return
    }
}

private fun AppCompatActivity.showErrorDialog(error: MIJError) {
    AlertDialog.Builder(this)
        .setTitle(error.message)
        .setMessage(error.description)
        .setPositiveButton("OK") { _, _ ->
            when(error.action) {
                DialogCloseOnly -> {}
                DialogLogout -> {
                    error.logoutAction?.invoke()
                    goToSplash()
                }
                DialogBack -> {
                    finish()
                }
                DialogRetry -> {
                    error.retryAction?.invoke()
                }
                else -> {}
            }
        }
        .setCancelable(
            when(error.action) {
                DialogLogout,
                DialogBack,
                DialogRetry -> false
                else -> true
            }
        )
        .show()
}

private fun AppCompatActivity.goToSplash() {
    val intent = Intent(this, SplashActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    this.startActivity(intent)
}