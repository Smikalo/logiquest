package com.example.demo1.api.stt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SttRequest(
    val config: Config,
    val content: String
)

@Serializable
data class Config(
    @SerialName("model")
    val model: String = "chirp_3",

    @SerialName("language_codes")
    val languageCodes: List<String> = listOf("en-US"),

    @SerialName("auto_decoding_config")
    val autoDecodingConfig: Map<String, String> = emptyMap()
)
