package de.groovybyte.chunky.magickexportplugin.utils

import se.llbit.chunky.JsonSettings
import se.llbit.json.Json
import se.llbit.json.JsonObject
import se.llbit.json.JsonString
import se.llbit.json.JsonValue

/**
 * @author Maximilian Stiede
 */
interface ChunkyJsonConverter<T> {
    fun fromJsonValue(value: JsonValue): T?
    fun toJsonValue(value: T): JsonValue?

    class BooleanJsonConverter(val defaultValue: Boolean) : ChunkyJsonConverter<Boolean> {
        override fun fromJsonValue(value: JsonValue) = value.boolValue(defaultValue)
        override fun toJsonValue(value: Boolean) = if (value) Json.TRUE else Json.FALSE
    }

    class StringJsonConverter(val defaultValue: String) : ChunkyJsonConverter<String> {
        override fun fromJsonValue(value: JsonValue) = value.stringValue(defaultValue)
        override fun toJsonValue(value: String) = JsonString(value)
    }
}

fun JsonObject.containsKey(key: String): Boolean =
    get(key) != Json.UNKNOWN

fun JsonObject.setOrRemove(
    key: String,
    value: JsonValue?
) {
    if (value == null) {
        remove(key)
    } else {
        set(key, value)
    }
}

fun JsonObject.getOrCreate(
    key: String,
    creator: (key: String) -> JsonValue?
): JsonValue {
    if (!containsKey(key)) {
        creator(key)?.let { set(key, it) }
    }
    return get(key)
}

fun JsonSettings.getOrCreate(
    key: String,
    creator: (key: String) -> JsonValue
): JsonValue {
    if (!containsKey(key)) {
        set(key, creator(key))
    }
    return get(key)
}

