package com.example.kt6_2.data.api.serializer

import com.example.kt6_2.data.api.models.LinksDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object LinksDeserializer : KSerializer<LinksDto?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LinksDto", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LinksDto?) {
    }

    override fun deserialize(decoder: Decoder): LinksDto? {
        val jsonDecoder = decoder as? JsonDecoder ?: return null
        val jsonElement = jsonDecoder.decodeJsonElement()

        return when (jsonElement) {
            is JsonObject -> {
                LinksDto(
                    rel = jsonElement["rel"]?.jsonPrimitive?.content,
                    href = jsonElement["href"]?.jsonPrimitive?.content,
                    action = jsonElement["action"]?.jsonPrimitive?.content,
                    types = jsonElement["types"]?.jsonPrimitive?.content
                )
            }
            is JsonArray -> {
                jsonElement.firstOrNull()?.jsonObject?.let { obj ->
                    LinksDto(
                        rel = obj["rel"]?.jsonPrimitive?.content,
                        href = obj["href"]?.jsonPrimitive?.content,
                        action = obj["action"]?.jsonPrimitive?.content,
                        types = obj["types"]?.jsonPrimitive?.content
                    )
                }
            }
            else -> null
        }
    }
}