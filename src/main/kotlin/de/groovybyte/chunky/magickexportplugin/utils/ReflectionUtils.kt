package de.groovybyte.chunky.magickexportplugin.utils

import java.lang.reflect.Field

/**
 * @author Maximilian Stiede
 */
inline fun <reified T> Field.isOfReadableType(): Boolean =
    T::class.java.isAssignableFrom(type)

inline fun <reified T> Field.getSafe(obj: Any): T = run {
    isAccessible = true
    get(obj) as T
}

inline fun <reified T> Any.getSafeFromField(fieldName: String) =
    this::class.java.getDeclaredField(fieldName).getSafe<T>(this)
