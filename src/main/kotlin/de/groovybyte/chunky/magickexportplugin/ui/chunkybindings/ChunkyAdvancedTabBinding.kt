package de.groovybyte.chunky.magickexportplugin.ui.chunkybindings

import de.groovybyte.chunky.magickexportplugin.magick.MagickExportFormat
import de.groovybyte.chunky.magickexportplugin.utils.findTabOfType
import de.groovybyte.chunky.magickexportplugin.utils.getSafeFromField
import javafx.beans.property.ObjectProperty
import javafx.scene.control.ChoiceBox
import se.llbit.chunky.renderer.export.PictureExportFormat
import se.llbit.chunky.ui.RenderControlsFxController
import se.llbit.chunky.ui.render.AdvancedTab
import tornadofx.cleanBind
import tornadofx.objectBinding

/**
 * @author Maximilian Stiede
 */
class ChunkyAdvancedTabBinding(
    controller: RenderControlsFxController
) {
    val tab: AdvancedTab = controller.findTabOfType<AdvancedTab>()
    val outputModeProperty: ObjectProperty<PictureExportFormat>

    init {
        val outputModeChoiceBox = tab
            .getSafeFromField<ChoiceBox<PictureExportFormat>>("outputMode")

//        outputModeChoiceBox.apply {
//            items.sortBy { it.name }
//            outputModeChoiceBox.requestLayout()
//        }
        outputModeProperty = outputModeChoiceBox.valueProperty()
    }

    fun bindExportFormat(
        exportFormatProperty: ObjectProperty<MagickExportFormat?>
    ) = exportFormatProperty.cleanBind(
        outputModeProperty
            .objectBinding { format ->
                when (format) {
                    is MagickExportFormat -> format
                    else -> null
                }
            }
    )
}
