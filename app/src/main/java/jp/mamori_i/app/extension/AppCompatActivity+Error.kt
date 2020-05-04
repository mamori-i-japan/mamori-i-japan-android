package jp.mamori_i.app.extension

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.start.SplashActivity

fun AppCompatActivity.handleError(error: MIJError) {
    when(error.action) {
        DialogCloseOnly,
        DialogRetry,
        DialogLogout,
        DialogBack,
        DialogAppKill -> {
            showErrorDialog(error)
        }
        Inline,
        InView -> return
        ForceLogout -> {
            error.closeAction?.invoke()
            goToSplash()
        }
        ForceScreenBack -> {
            finish()
        }
        else -> return
    }
}

private fun AppCompatActivity.showErrorDialog(error: MIJError) {

    val completion: (() -> Unit)? = when(error.action) {
        DialogCloseOnly -> null
        DialogLogout -> { ->
            error.closeAction?.invoke()
            goToSplash()
        }
        DialogBack -> { ->
            finish()
        }
        DialogRetry -> { ->
            error.closeAction?.invoke()
        }
        DialogAppKill -> { ->
            error.closeAction?.invoke()
            appKill()
        }
        else -> null
    }

    showAlertDialog(
        error.message,
        error.description,
        completion
    )
}

private fun AppCompatActivity.goToSplash() {
    val intent = Intent(this, SplashActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    this.startActivity(intent)
}

private fun AppCompatActivity.appKill() {
    finish()
    moveTaskToBack(true)
}