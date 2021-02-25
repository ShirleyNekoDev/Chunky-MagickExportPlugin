package de.groovybyte.chunky.magickexportplugin.chunky

/**
 * @author Maximilian Stiede
 */
data class RGBPixelBuffer(
    val width: Int,
    val height: Int,
    val rgbData: DoubleArray,
) {
    fun forEachPixel(consumer: RGBPixelConsumer) {
        for (i in rgbData.indices step 3) {
            consumer(rgbData[i], rgbData[i + 1], rgbData[i + 2])
        }
    }

    override fun toString(): String =
        "RGBPixelBuffer(width=$width, height=$height, bufferLength=${rgbData.size})"

    fun interface RGBPixelConsumer {
        fun consumePixel(r: Double, g: Double, b: Double)
        operator fun invoke(r: Double, g: Double, b: Double) = consumePixel(r, g, b)
    }
}
