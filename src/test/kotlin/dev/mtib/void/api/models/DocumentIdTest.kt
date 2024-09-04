package dev.mtib.void.api.models

import io.kotest.core.spec.style.FunSpec

class DocumentIdTest: FunSpec({
    test("DocumentId.generate") {
        val id = DocumentId.generate()
        assert(id.identifier.matches(Regex("d-[0-9a-f]{8}")))
    }

    test("DocumentId.matchesSchema") {
        assert(DocumentId.matchesSchema("d-12345678"))
        assert(!DocumentId.matchesSchema("d-1234567"))
        assert(!DocumentId.matchesSchema("d-123456789"))
    }

    test("DocumentId.equals") {
        val id1 = DocumentId("d-12345678")
        val id2 = DocumentId("d-12345678")
        val id3 = DocumentId("d-12345679")
        assert(id1 == id2)
        assert(id1 != id3)
    }

    test("Generate 1000 DocumentIds") {
        val ids = (0..1000).map { DocumentId.generate() }
        assert(ids.size == ids.distinct().size)
    }

    test("Generate 1000 DocumentIds and check schema") {
        val ids = (0..1000).map { DocumentId.generate() }
        assert(ids.all { DocumentId.matchesSchema(it.identifier) })
    }
})