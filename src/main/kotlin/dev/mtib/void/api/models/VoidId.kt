package dev.mtib.void.api.models

import dev.mtib.void.api.utils.Crypto

data class VoidId(val identifier: String) {
    companion object {
        fun generate(): VoidId {
            return VoidId("v-${Crypto.randomHexHash().substring(0..<16)}")
        }
    }
}