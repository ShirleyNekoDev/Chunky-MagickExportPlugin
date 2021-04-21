package de.groovybyte.chunky.magickexportplugin.magick

import de.groovybyte.chunky.magickexportplugin.MagickExportPlugin
import de.groovybyte.chunky.magickexportplugin.chunky.facade
import se.llbit.chunky.renderer.export.PictureExportFormat
import se.llbit.chunky.renderer.scene.Scene
import se.llbit.util.TaskTracker
import java.io.OutputStream


/**
 * @author Maximilian Stiede
 */
open class MagickExportFormat(
    val _name: String,
    val _extension: String,
    val _description: String,
    val magickFormat: String,
    val additionalMagickParameters: List<String> = emptyList()
) : PictureExportFormat {
    override fun getName() = "[Magick] $_name"
    override fun getExtension() = ".$_extension"
    override fun getDescription() = "[${MagickExportPlugin.NAME}] $_description"

    /**
     * TODO: Magick export currently does not support transparency
     */
    override fun isTransparencySupported() = false

    override fun write(outputStream: OutputStream, scene: Scene, taskTracker: TaskTracker) =
        taskTracker
            .task("Exporting using Magick")
            .use {
                MagickExport.export(
                    scene.facade.rgbPixelBuffer,
                    outputStream,
                    this
                )
            }

    object EXR : MagickExportFormat(
        "OpenEXR",
        "exr",
        "OpenEXR format (lossless, 16-bit floating-point)",
        "EXR",
        listOf(
            "-define exr:color-type=RGB",
//        "-sampling-factor 4:2:0", // requires exr:color-type=YC(A)
            "-compress Piz", // good for grainy images (lossless)
        )
    )

    object PFM : MagickExportFormat(
        "PFM",
        "pfm",
        "Portable Float Map (lossless, 32-bit floating-point)",
        "PFM"
    )

    object PNG8 : MagickExportFormat(
        "PNG (RGB 8)",
        "png",
        "Portable Network Graphics (lossless, 8-bit integer)",
        "PNG24"
    )

    object PNG16 : MagickExportFormat(
        "PNG (RGB 16)",
        "png",
        "Portable Network Graphics (lossless, 16-bit integer)",
        "PNG48"
    )
}
