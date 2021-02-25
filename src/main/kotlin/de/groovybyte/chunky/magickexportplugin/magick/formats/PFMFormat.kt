package de.groovybyte.chunky.magickexportplugin.magick.formats

import de.groovybyte.chunky.magickexportplugin.magick.ExportFormat

/**
 * @author Maximilian Stiede
 */
object PFMFormat : ExportFormat {
    override val name = "Portable Float Map"
    override val extension = "pfm"
    override val magickFormat = "PFM"

}
