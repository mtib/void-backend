package dev.mtib.void.api

import dev.mtib.void.api.rest.DocumentRoutes
import dev.mtib.void.api.rest.VoidRoutes
import io.ktor.http.*
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
                    methods.add(HttpMethod.Put)
                }
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    with(VoidRoutes) {
                        this@embeddedServer.registerVoidRoutes()
                    }
                    with(DocumentRoutes){
                        this@embeddedServer.registerDocumentRoutes()
                    }
                }
            }
        ).start(wait = true)
    }
}