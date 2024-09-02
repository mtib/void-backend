package dev.mtib.void.api.storage

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import dev.mtib.void.api.models.DocumentId
import dev.mtib.void.api.models.VoidId
import dev.mtib.void.api.models.VoidMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.*

/**
 * This technically requires synchronisation, lets deal with that later.
 */
class FileStorage(
    private val root: Path
) : StorageInterface {
    init {
        if (root.isRegularFile()) {
            throw IllegalStateException("root exists as file")
        }
        if (!root.exists()) {
            root.createDirectory()
        }
    }

    private fun VoidId.toPath() = root.resolve(identifier)

    override suspend fun createVoid(id: VoidId) {
        if (id.toPath().exists()) {
            throw IllegalStateException("void already exists")
        }
        root.resolve(id.identifier).createDirectory()
    }

    override suspend fun readVoidMetadata(id: VoidId): VoidMetadata {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val metadataPath = voidPath.resolve("metadata.json")
        if (!metadataPath.exists()) {
            return VoidMetadata()
        }
        return Json.decodeFromString(metadataPath.readText())
    }

    override suspend fun updateVoidMetadata(id: VoidId, metadata: VoidMetadata) {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val metadataPath = voidPath.resolve("metadata.json")
        metadataPath.writeText(Json.encodeToString(metadata))
    }

    @OptIn(ExperimentalPathApi::class)
    override suspend fun deleteVoid(id: VoidId) {
        if (!id.toPath().exists()) {
            throw IllegalStateException("void does not exist")
        }
        id.toPath().deleteRecursively()
    }

    override suspend fun createDocument(id: VoidId, documentId: DocumentId, body: ByteArray?) {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val documentPath = voidPath.resolve(documentId.identifier)
        if (documentPath.exists()) {
            throw IllegalStateException("document already exists")
        }
        documentPath.writeBytes(body ?: byteArrayOf())
    }

    override suspend fun readDocument(id: VoidId, documentId: DocumentId): Option<ByteArray> {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val documentPath = voidPath.resolve(documentId.identifier)
        if (!documentPath.exists()) {
            return none()
        }
        return documentPath.readBytes().some()
    }

    override suspend fun updateDocument(id: VoidId, documentId: DocumentId, body: ByteArray) {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val documentPath = voidPath.resolve(documentId.identifier)
        if (!documentPath.exists()) {
            throw IllegalStateException("document does not exist")
        }
        documentPath.writeBytes(body)
    }

    override suspend fun deleteDocument(id: VoidId, documentId: DocumentId) {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        val documentPath = voidPath.resolve(documentId.identifier)
        if (!documentPath.exists()) {
            throw IllegalStateException("document does not exist")
        }
        documentPath.deleteExisting()
    }

    override suspend fun iterateDocuments(id: VoidId): Flow<DocumentId> {
        val voidPath = id.toPath()
        if (!voidPath.exists()) {
            throw IllegalStateException("void does not exist")
        }
        return voidPath.listDirectoryEntries().sortedBy {
            -Files.readAttributes(it, BasicFileAttributes::class.java).creationTime().toMillis()
        }.map { DocumentId(it.fileName.toString()) }
            .asFlow()
    }
}