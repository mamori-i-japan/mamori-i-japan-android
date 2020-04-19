package jp.co.tracecovid19.data.model

data class RemoteConfig(val minimumVersion: String,
                        val isMaintenance: Boolean,
                        val storeUrl: String) {
}