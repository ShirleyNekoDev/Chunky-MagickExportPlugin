package de.groovybyte.chunky.magickexportplugin.magick.formats

import de.groovybyte.chunky.magickexportplugin.magick.ExportFormat

/**
 * @author Maximilian Stiede
 */
object PNG8BitFormat : ExportFormat {
    override val name = "Portable Network Graphics 8bit"
    override val extension = "png"
    override val magickFormat = "PNG24"

}

object PNG16BitFormat : ExportFormat {
    override val name = "Portable Network Graphics 16bit"
    override val extension = "png"
    override val magickFormat = "PNG48"

}
