package com.example.demo1

import com.example.demo1.api.stt.STTService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import java.io.File

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        post("/tts") {
            val res = Thread.currentThread().contextClassLoader.getResource("audio/test.wav") ?: error("not")

            val file = File(res.toURI())
            val bytes = file.readBytes()
            val stt = STTService(
                projectId = "hackatum25mun-1088",
                location = "eu",
            )

            val trans = stt.recognize(
                audioBytes = bytes,
                langCode = "en-US"
            )

            call.respondText(trans)
        }
    }
}