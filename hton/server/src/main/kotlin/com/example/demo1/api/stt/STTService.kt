package com.example.demo1.api.stt

import com.google.cloud.speech.v2.RecognitionConfig
import com.google.cloud.speech.v2.SpeechClient
import com.google.cloud.speech.v2.RecognizerName
import com.google.cloud.speech.v2.AutoDetectDecodingConfig
import com.google.cloud.speech.v2.SpeechSettings
import com.google.protobuf.ByteString
import com.google.protobuf.FieldMask

class STTService(
    private val projectId: String,
    private val location: String = "global",
    private val recId: String = "_"
) {
    fun recognize(audioBytes: ByteArray, langCode: String): String {
        val settings = SpeechSettings.newBuilder()
            .setEndpoint("eu-speech.googleapis.com:443")
            .build()

        SpeechClient.create(settings).use { client ->
            val recName = RecognizerName.of(projectId, location, recId)

            val config = RecognitionConfig.newBuilder()
                .setModel("chirp_3")
                .addLanguageCodes(langCode)
                .setAutoDecodingConfig(
                    AutoDetectDecodingConfig.getDefaultInstance()
                )
                .build()

            val configMask = FieldMask.newBuilder()
                .addPaths("model")
                .addPaths("language_codes")
                .addPaths("auto_decoding_config")
                .build()

            val content = ByteString.copyFrom(audioBytes)

            val response = client.recognize(recName, config, configMask, content)

            val res = response.resultsList.firstOrNull()?.alternativesList?.firstOrNull()

            return res?.transcript ?: ""
        }
    }
}