package de.groovybyte.chunky.magickexportplugin.magick.formats

import de.groovybyte.chunky.magickexportplugin.magick.ExportFormat

/**
 * @author Maximilian Stiede
 */
object EXRFormat : ExportFormat {
    override val name = "OpenEXR"
    override val extension = "exr"
    override val magickFormat = "EXR"

    override val additionalMagickParameters = listOf(
        "-define exr:color-type=RGB",
//        "-sampling-factor 4:2:0", // requires exr:color-type=YC(A)
        "-compress Piz", // good for grainy images (lossless)
    )
}
