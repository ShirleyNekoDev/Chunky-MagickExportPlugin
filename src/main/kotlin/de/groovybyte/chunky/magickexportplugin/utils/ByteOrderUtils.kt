package de.groovybyte.chunky.magickexportplugin.utils

import java.nio.ByteOrder

/**
 * @author Maximilian Stiede
 */
val ByteOrder.shortName: String
    get() = when (this) {
        ByteOrder.LITTLE_ENDIAN -> "LSB"
        else -> "MSB"
    }
val ByteOrder.longName: String
    get() = when (this) {
        ByteOrder.LITTLE_ENDIAN -> "Little Endian"
        else -> "Big Endian"
    }
