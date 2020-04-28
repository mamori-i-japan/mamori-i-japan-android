package jp.mamori_i.app.data.storage

import java.security.Key
import java.security.PublicKey


interface MIJKeyStore {
    fun publicKey(): PublicKey
    fun privateKey(): Key
}