package de.groovybyte.chunky.magickexportplugin.magick

import de.groovybyte.chunky.magickexportplugin.MagickExportConfig.persisted
import de.groovybyte.chunky.magickexportplugin.chunky.RGBPixelBuffer
import de.groovybyte.chunky.magickexportplugin.utils.ChunkyJsonConverter
import de.groovybyte.chunky.magickexportplugin.utils.PropertyResetGroup
import de.groovybyte.chunky.magickexportplugin.utils.resettable
import de.groovybyte.chunky.magickexportplugin.utils.shortName
import javafx.beans.property.SimpleObjectProperty
import se.llbit.json.JsonString
import se.llbit.json.JsonValue
import se.llbit.log.Log
import tornadofx.*
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.ByteOrder

/**
 * @author Maximilian Stiede
 */
object MagickExport {

    val magickExecutablePathProperty = SimpleObjectProperty<String?>()
        .persisted(
            "executableLocation",
            object : ChunkyJsonConverter<String?> {
                override fun fromJsonValue(value: JsonValue) =
                    value.asString(null)

                override fun toJsonValue(value: String?) =
                    value?.let { JsonString(it) }
            }
        )
    var magickExecutablePath: String? by magickExecutablePathProperty

    // Start of Expert Settings -------------

    val expertSettingsResetGroup = PropertyResetGroup()

    val endiannessProperty = SimpleObjectProperty(ByteOrder.BIG_ENDIAN)
        .resettable(expertSettingsResetGroup)
        .persisted(
            "endianness",
            object : ChunkyJsonConverter<ByteOrder> {
                override fun fromJsonValue(value: JsonValue) =
                    when (value.asString(null)) {
                        "LSB" -> ByteOrder.LITTLE_ENDIAN
                        else -> ByteOrder.BIG_ENDIAN
                    }

                override fun toJsonValue(value: ByteOrder) =
                    JsonString(value.shortName)
            }
        )
    var endianness: ByteOrder by endiannessProperty

    // End of Expert Settings -------------

    /**
     * the targetFile extension is automatically changed to the correct type
     */
    fun export(
        buffer: RGBPixelBuffer,
        outputStream: OutputStream,
        format: MagickExportFormat
    ) {
        Log.info("Exporting using magick (format: ${format._name})")
        Magick(
            listOf(
                "convert",
                "-size ${buffer.width}x${buffer.height}",
                "-depth 64",
                "-define quantum:format=floating-point",
                "-define quantum:minimum=0.0",
                "-define quantum:maximum=1.0",
                "-colorspace RGB", //sRGB
                "-endian ${endianness.shortName}",
            ) + format.additionalMagickParameters + listOf(
                "RGB:-",
                "\"${format.magickFormat}:-\"",
            )
        ).use {
            it.writeBuffer(buffer)
            it.readResult(outputStream)
        }
        Log.info("Export completed")
    }

    private class Magick(
        parameters: List<String>
    ) : AutoCloseable {
        val process: Process

        init {
            val params = parameters.joinToString(
                prefix = "${magickExecutablePath ?: "magick"} ",
                separator = " "
            )
            Log.info("Magick parameters: $params")
            process = ProcessBuilder(params.split(' ')).run {
                redirectErrorStream(true)
                redirectOutput(ProcessBuilder.Redirect.PIPE)
                Log.info("Starting magick process (wish me luck)")
                start()
            }
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
                }
            }
        }

        fun readResult(outputStream: OutputStream) {
            process.inputStream.use { processOut ->
                // Java 9: transferTo
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var read: Int
                while (processOut.read(buffer, 0, DEFAULT_BUFFER_SIZE).also { read = it } >= 0) {
                    outputStream.write(buffer, 0, read)
                }
            }
        }

        override fun close() {
            Log.info("Waiting for magick to finish")
            process.waitFor()
        }
    }
}
