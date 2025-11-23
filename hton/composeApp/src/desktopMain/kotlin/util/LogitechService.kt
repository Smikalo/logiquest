package util

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

object LogitechService {
    private val _lastAction = MutableStateFlow<String?>(null)
    val lastAction = _lastAction.asStateFlow()

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            // Start a local HTTP server to listen to the C# Plugin
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/action/{type}") {
                        val type = call.parameters["type"] ?: ""
                        println("🔥 SDK ACTION RECEIVED: $type")

                        // Map C# parameter to internal action format
                        val actionId = when(type) {
                            "attack" -> "action.hero_attack"
                            "shield" -> "action.hero_shield"
                            else -> null
                        }

                        if (actionId != null) {
                            _lastAction.value = actionId
                            // Reset after short delay
                            launch {
                                delay(500)
                                _lastAction.value = null
                            }
                        }

                        call.respondText("OK")
                    }
                }
            }.start(wait = true)
        }
    }
}