package com.lukeneedham.languagetransfer.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

/** Saves a [File] as its path, so a navigation key holding one can be saved and restored. */
object FileAsPathSerializer : KSerializer<File> {
    override val descriptor = PrimitiveSerialDescriptor("java.io.File", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: File) = encoder.encodeString(value.path)

    override fun deserialize(decoder: Decoder) = File(decoder.decodeString())
}
