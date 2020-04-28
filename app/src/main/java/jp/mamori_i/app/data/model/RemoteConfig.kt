package jp.mamori_i.app.data.model

data class RemoteConfig(val minimumVersion: String,
                        val isMaintenance: Boolean,
                        val storeUrl: String) {
}