package de.groovybyte.chunky.magickexportplugin.magick

import de.groovybyte.chunky.magickexportplugin.magick.formats.EXRFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PFMFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PNG16BitFormat
import de.groovybyte.chunky.magickexportplugin.magick.formats.PNG8BitFormat


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

        operator fun get(name: String) = REGISTRY.find { it.name == name }
    }
}
