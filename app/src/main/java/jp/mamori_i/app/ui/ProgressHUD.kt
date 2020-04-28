package jp.mamori_i.app.ui

import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD


class ProgressHUD {
    companion object {
        private var hud: KProgressHUD? = null
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
            hud = KProgressHUD.create(context, KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show()
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