package dev.mtib.void.api.utils

import java.security.MessageDigest
import java.time.Instant
import kotlin.random.Random

object Crypto {
    fun randomHexHash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(Instant.now().toString().toByteArray().plus(Random.nextBytes(16)))
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}