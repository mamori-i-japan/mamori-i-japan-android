package jp.co.tracecovid19.data.storage

import java.security.Key
import java.security.PublicKey


interface TraceCovid19KeyStore {
    fun publicKey(): PublicKey
    fun privateKey(): Key
}