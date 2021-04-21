package de.groovybyte.chunky.magickexportplugin.utils

import javafx.beans.property.StringProperty
import javafx.scene.control.ComboBox
import se.llbit.chunky.ui.RenderControlsFxController
import se.llbit.chunky.ui.render.GeneralTab
import se.llbit.chunky.ui.render.RenderControlsTab
import tornadofx.onChange

/**
 * @author Maximilian Stiede
 */
private val VALID_CANVAS_SIZE_REGEX = Regex("([0-9]+)[xX.*]([0-9]+)")
fun tryBindingCanvasSizeProperty(
    sizeProperty: StringProperty,
    controller: RenderControlsFxController
) {
    val updateSize = { size: String? ->
        if (size != null && size.matches(VALID_CANVAS_SIZE_REGEX)) {
            sizeProperty.set(size)
        }
    }
    try {
        controller.findTabOfType<GeneralTab>()
            .let { generalTab ->
                generalTab::class.java
                    .getSafeFromField<ComboBox<String>>("canvasSize")
                    .valueProperty()
            }
            .apply {
                value.also(updateSize)
                onChange(updateSize)
            }
    } catch (ex: Exception) {
    }
}

inline fun <reified T> RenderControlsFxController.findTabOfType() = this
    .getSafeFromField<Collection<RenderControlsTab>>("tabs")
    .filterIsInstance<T>()
    .first()
