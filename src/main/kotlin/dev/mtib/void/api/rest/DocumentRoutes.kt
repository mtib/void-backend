package dev.mtib.void.api.rest

import arrow.core.None
import arrow.core.Some
import dev.mtib.void.api.models.DocumentId
import dev.mtib.void.api.models.VoidId
import dev.mtib.void.api.storage.EnvFileStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant

object DocumentRoutes {
    fun Application.registerDocumentRoutes() {
        routing {
            route("/document") {
                createDocument()
                editDocument()
                readDocument()
            }
        }
    }

    private fun Route.createDocument() {
        post {
            val voidId = VoidId(call.request.queryParameters["voidId"] ?: return@post call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val documentId = DocumentId.generate()
            val body = call.receive<ByteArray>()

            EnvFileStorage.createDocument(voidId, documentId, body)

            call.respond(buildJsonObject {
                put("voidId", voidId.identifier)
                put("documentId", documentId.identifier)
                put("size", body.size)
                put("approximateTime", Instant.now().epochSecond)
            })
        }
    }

    private fun Route.editDocument() {
        put("/{voidId}/{documentId}") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@put call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val documentId = DocumentId(call.parameters["documentId"] ?: return@put call.respondText("Missing documentId", status = HttpStatusCode.BadRequest))
            val body = call.receive<ByteArray>()

            EnvFileStorage.updateDocument(voidId, documentId, body)

            call.respond(buildJsonObject {
                put("voidId", voidId.identifier)
                put("documentId", documentId.identifier)
                put("size", body.size)
            })
        }
    }

    private fun Route.readDocument() {
        get("/{voidId}/{documentId}") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@get call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val documentId = DocumentId(call.parameters["documentId"] ?: return@get call.respondText("Missing documentId", status = HttpStatusCode.BadRequest))
            val document = EnvFileStorage.readDocument(voidId, documentId)

            when (document) {
                is None -> call.respondText("Document not found", status = HttpStatusCode.NotFound)
                is Some -> call.respondBytes(document.value)
            }
        }
    }
}