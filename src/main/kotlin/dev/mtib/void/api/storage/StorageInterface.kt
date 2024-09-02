package dev.mtib.void.api.storage

import arrow.core.Option
import dev.mtib.void.api.models.DocumentId
import dev.mtib.void.api.models.VoidId
import dev.mtib.void.api.models.VoidMetadata
import kotlinx.coroutines.flow.Flow

interface StorageInterface {
    suspend fun createVoid(id: VoidId)
    suspend fun readVoidMetadata(id: VoidId): VoidMetadata
    suspend fun updateVoidMetadata(id: VoidId, metadata: VoidMetadata)
    suspend fun deleteVoid(id: VoidId)

    suspend fun createDocument(id: VoidId, documentId: DocumentId, body: ByteArray?)
    suspend fun readDocument(id: VoidId, documentId: DocumentId): Option<ByteArray>
    suspend fun updateDocument(id: VoidId, documentId: DocumentId, body: ByteArray)
    suspend fun deleteDocument(id: VoidId, documentId: DocumentId)

    suspend fun iterateDocuments(id: VoidId): Flow<DocumentId>
}