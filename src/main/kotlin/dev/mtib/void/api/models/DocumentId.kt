package dev.mtib.void.api.models

import dev.mtib.void.api.utils.Crypto

data class DocumentId(val identifier: String) {
    companion object {
        fun generate(): DocumentId {
            return DocumentId("d-${Crypto.randomHexHash().substring(0..<8)}")
        }
        fun matchesSchema(id: String): Boolean {
            return id.matches(Regex("d-[0-9a-f]{8}"))
        }
    }
}