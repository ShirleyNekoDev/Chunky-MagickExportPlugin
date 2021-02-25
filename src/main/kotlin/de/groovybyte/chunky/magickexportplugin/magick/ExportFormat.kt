package de.groovybyte.chunky.magickexportplugin.magick

import de.groovybyte.chunky.magickexportplugin.magick.formats.EXRFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PFMFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PNG16BitFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PNG8BitFormat
import de.groovybyte.chunky.magickexportplugin.utils.ChunkyJsonConverter
import se.llbit.json.JsonString
import se.llbit.json.JsonValue


/**
 * @author Maximilian Stiede
 */
interface ExportFormat {
    val name: String
    val extension: String
    val magickFormat: String
    val additionalMagickParameters: List<String>
        get() = emptyList()

    companion object {
        val REGISTRY = mutableSetOf<ExportFormat>(
            EXRFormat,
            PFMFormat,
            PNG8BitFormat,
            PNG16BitFormat
        )
        val DEFAULT by lazy { REGISTRY.first() }

        operator fun get(name: String) = REGISTRY.find { it.name == name }
    }

    fun toJsonString() = name

    object ExportFormatJsonConverter : ChunkyJsonConverter<ExportFormat> {
        override fun fromJsonValue(value: JsonValue) =
            get(value.stringValue(DEFAULT.toJsonString()))

        override fun toJsonValue(value: ExportFormat): JsonValue =
            JsonString(value.toJsonString())
    }
}
