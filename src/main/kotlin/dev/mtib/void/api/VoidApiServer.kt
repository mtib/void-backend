package dev.mtib.void.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object VoidApiServer {
    fun start() {
        embeddedServer(
            Netty,
            port = 49420,
            module = {
                install(StatusPages)
                install(CORS) {
                    anyHost()
                }
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    get("/") {
                        call.respond(mapOf(
                            "message" to "Hello Void!"
                        ))
                    }
                }
            }
        ).start(wait = true)
    }
}