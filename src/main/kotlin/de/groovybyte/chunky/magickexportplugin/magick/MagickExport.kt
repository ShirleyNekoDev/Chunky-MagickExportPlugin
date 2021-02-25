package de.groovybyte.chunky.magickexportplugin.magick

import de.groovybyte.chunky.magickexportplugin.chunky.RGBPixelBuffer
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.io.DataOutputStream
import java.io.File
import kotlin.error

/**
 * @author Maximilian Stiede
 */
object MagickExport {

    val magickExecutableProperty = SimpleObjectProperty<File?>()
    var magickExecutable: File? by magickExecutableProperty

    /**
     * the targetFile extension is automatically changed to the correct type
     */
    fun export(
        buffer: RGBPixelBuffer,
        targetFile: File,
        format: ExportFormat
    ): File {
        val file = targetFile.resolveSibling(
            "${targetFile.nameWithoutExtension}.${format.extension}"
        )
        Magick(
            listOf(
                "convert",
                "-size ${buffer.width}x${buffer.height}",
                "-depth 64",
                "-define quantum:format=floating-point",
                "-define quantum:minimum=0.0",
                "-define quantum:maximum=1.0",
                "-colorspace RGB", //sRGB
                "-endian MSB",
            ) + format.additionalMagickParameters + listOf(
                "RGB:-",
                "\"${format.magickFormat}:$file\"",
            )
        ).use {
            it.writeBuffer(buffer)
        }
        return file
    }

    private class Magick(parameters: List<String>) : AutoCloseable {
        val process: Process

        init {
            val params = parameters.joinToString(
                prefix = "${magickExecutable ?: "magick"} ",
                separator = " "
            )
            println("Magick parameters: $params")
            process = ProcessBuilder(params.split(' ')).run {
                redirectErrorStream(true)
                redirectOutput(ProcessBuilder.Redirect.INHERIT)
                start()
            }
            println("Started magick as pid=${process.pid()}")
            if (!process.isAlive) {
                error("Magick process died")
            }
        }

        fun writeBuffer(buffer: RGBPixelBuffer) {
            DataOutputStream(process.outputStream.buffered()).use { out ->
                buffer.forEachPixel { r, g, b ->
                    // .replaceNaN(0.0) // maybe replace NaN ?
                    // .coerceIn(0.0, 1.0) // maybe clamp values ?
                    out.writeDouble(r)
                    out.writeDouble(g)
                    out.writeDouble(b)
                    /*
                        If byte order is wrong use:
                        ByteBuffer.allocate(8)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putDouble(d)
                            .array()
                     */
                }
            }
        }

        override fun close() {
            println("Waiting for magick to finish")
            process.waitFor()
        }
    }
}
