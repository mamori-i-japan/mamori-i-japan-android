package jp.co.tracecovid19.data.model

data class FirebaseStorageData(val data: ByteArray?,
                               val generation: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirebaseStorageData

        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (generation != other.generation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.contentHashCode() ?: 0
        result = 31 * result + generation.hashCode()
        return result
    }
}