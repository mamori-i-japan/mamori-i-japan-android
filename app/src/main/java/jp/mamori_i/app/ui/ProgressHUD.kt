package jp.mamori_i.app.ui

import android.app.Dialog
import android.content.Context
import android.view.Window
import jp.mamori_i.app.R


class ProgressHUD {
    companion object {
        private var hud: Dialog? = null
        private var owner: Context? = null

        fun show(context: Context) {
            hud?.let {
                if (it.isShowing && sameOwner(context)) {
                    // 同じオーナーで表示中の場合は何もしない
                    return
                } else {
                    // 一回閉じる
                    it.dismiss()
                }
            }
            hud = Dialog(context).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_progress_hud)
                setCancelable(false)
                show()
            }
            owner = context
        }

        fun hide() {
            hud?.dismiss()
        }

        private fun sameOwner(context: Context): Boolean {
            owner?.let {
                return it == context
            }?: return false
        }
    }
}