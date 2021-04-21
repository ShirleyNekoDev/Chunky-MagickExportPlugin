package de.groovybyte.chunky.magickexportplugin.utils

import javafx.beans.property.Property

/**
 * @author Maximilian Stiede
 */
class PropertyResetGroup {
    val propertyDefaultValues = mutableMapOf<Property<out Any?>, Any?>()

    operator fun plusAssign(property: Property<out Any?>) {
        propertyDefaultValues[property] = property.value
    }

    @Suppress("UNCHECKED_CAST")
    val <T : Any?, P : Property<T>> P.defaultValue: T?
        get() = propertyDefaultValues[this]?.let { it as T }

    fun resetAll() {
        propertyDefaultValues.forEach { property, value ->
            property.value = value
        }
    }
}

fun <T : Any?, P : Property<T>> P.resettable(
    propertyResetGroup: PropertyResetGroup?
) = apply {
    propertyResetGroup?.let { it += this }
}
