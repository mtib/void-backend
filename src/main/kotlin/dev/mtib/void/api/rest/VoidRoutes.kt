package dev.mtib.void.api.rest

import arrow.core.some
import arrow.core.toOption
import dev.mtib.void.api.models.VoidId
import dev.mtib.void.api.models.VoidMetadata
import dev.mtib.void.api.storage.EnvFileStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.*

object VoidRoutes {
    fun Application.registerVoidRoutes() {
        routing {
            route("/void") {
                createVoid()
                listDocuments()
                deleteVoid()

                setMetadata()
                readMetadata()
            }
        }
    }

    private fun Route.createVoid() {
        post {

            val id = VoidId.generate()
            EnvFileStorage.createVoid(id)

            call.respond(buildJsonObject {
                put("id", id.identifier)
            })
        }
    }

    private fun Route.listDocuments() {
        get("/{voidId}/documents") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@get call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val documents = EnvFileStorage.iterateDocuments(voidId).toList()

            call.respond(buildJsonObject {
                put("voidId", voidId.identifier)
                put("documents", buildJsonArray {
                    documents.forEach { add(it.identifier) }
                })
            })
        }
    }

    private fun Route.deleteVoid() {
        delete("/{voidId}") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@delete call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            EnvFileStorage.deleteVoid(voidId)
            call.respond(HttpStatusCode.NoContent)
        }
    }

    private fun Route.setMetadata() {
        put("/{voidId}/metadata") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@put call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val metadata = VoidMetadata(
                call.receiveText().let {
                    if (it.isBlank()) {
                        null
                    } else {
                        Json.decodeFromString<Map<String, String>>(it)
                    }
                }.toOption()
            )
            EnvFileStorage.updateVoidMetadata(voidId, metadata)
            call.respond(HttpStatusCode.NoContent)
        }
    }

    private fun Route.readMetadata() {
        get("/{voidId}/metadata") {
            val voidId = VoidId(call.parameters["voidId"] ?: return@get call.respondText("Missing voidId", status = HttpStatusCode.BadRequest))
            val metadata = EnvFileStorage.readVoidMetadata(voidId)
            call.respond(metadata)
        }
    }
}