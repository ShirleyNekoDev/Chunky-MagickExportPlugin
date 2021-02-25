package de.groovybyte.chunky.magickexportplugin

import de.groovybyte.chunky.magickexportplugin.utils.ChunkyJsonConverter
import de.groovybyte.chunky.magickexportplugin.utils.getOrCreate
import de.groovybyte.chunky.magickexportplugin.utils.setOrRemove
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import se.llbit.chunky.PersistentSettings
import se.llbit.json.JsonObject
import tornadofx.*

/**
 * @author Maximilian Stiede
 */
object MagickExportConfig {

    val configRootObject by lazy<JsonObject> {
        PersistentSettings.settings
            .getOrCreate("pluginConfigurations") { JsonObject(1) }
            .asObject()
            .run { getOrCreate("magickExportPlugin") { JsonObject() } }
            .asObject()
    }

    val configChangedProperty = SimpleBooleanProperty(false)
    var configChanged: Boolean by configChangedProperty

    fun save() {
        synchronized(MagickExportConfig) {
            if (configChanged) {
                PersistentSettings::class.java.getDeclaredMethod("save").apply {
                    isAccessible = true
                    invoke(null)
                }
                configChanged = false
            }
        }
    }

    fun persistentProperty(
        configKey: String,
        initialValue: Boolean
    ) = persistentProperty(
        configKey,
        initialValue,
        ChunkyJsonConverter.BooleanJsonConverter(initialValue)
    )

    fun persistentProperty(
        configKey: String,
        initialValue: String
    ) = persistentProperty(
        configKey,
        initialValue,
        ChunkyJsonConverter.StringJsonConverter(initialValue)
    )

    fun <T> persistentProperty(
        configKey: String,
        initialValue: T,
        jsonizer: ChunkyJsonConverter<T>
    ) = property(initialValue)
        .apply { fxProperty.persisted(configKey, jsonizer) }

    fun <T, P : Property<T>> P.persisted(
        configKey: String,
        jsonizer: ChunkyJsonConverter<T>
    ) = apply {
        value = jsonizer.fromJsonValue(
            configRootObject.getOrCreate(configKey) { jsonizer.toJsonValue(value) }
        )
        onChange {
            configRootObject.setOrRemove(configKey, it?.let(jsonizer::toJsonValue))
            configChanged = true
        }
    }
}
