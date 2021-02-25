package de.groovybyte.chunky.magickexportplugin.ui

import de.groovybyte.chunky.magickexportplugin.MagickExportConfig
import de.groovybyte.chunky.magickexportplugin.MagickExportConfig.persistentProperty
import de.groovybyte.chunky.magickexportplugin.chunky.facade
import de.groovybyte.chunky.magickexportplugin.magick.ExportFormat
import de.groovybyte.chunky.magickexportplugin.magick.MagickExport
import de.groovybyte.chunky.magickexportplugin.magick.formats.EXRFormat
import de.groovybyte.chunky.magickexportplugin.utils.taskTracker
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.StringConverter
import se.llbit.chunky.main.Chunky
import se.llbit.chunky.renderer.RenderContext
import se.llbit.chunky.renderer.RenderMode
import se.llbit.chunky.renderer.scene.Scene
import se.llbit.chunky.renderer.scene.SceneManager
import se.llbit.chunky.ui.render.RenderControlsTab
import se.llbit.log.Log
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.nio.file.Files

/**
 * @author Maximilian Stiede
 */
class MagickExportTab(
    val chunky: Chunky
) : RenderControlsTab, Fragment() {

    override fun getTabTitle(): String = "MagickExport"

    override fun getTabContent(): Node = root

    fun exportFormatProperty() = getProperty(MagickExportTab::exportFormat)
    var exportFormat by persistentProperty(
        "exportFormat",
        EXRFormat,
        ExportFormat.ExportFormatJsonConverter
    )

    fun openAfterExportProperty() = getProperty(MagickExportTab::openAfterExport)
    var openAfterExport by persistentProperty("openAfterExport", true)

    fun currentlyExportingProperty() = getProperty(MagickExportTab::currentlyExporting)
    var currentlyExporting by property(false)

    private val context: RenderContext get() = chunky.renderContext
    private val sceneManager: SceneManager get() = chunky.sceneManager

    private fun onInitialized() {
        MagickExportConfig.configChangedProperty.onChange {
            if (it) {
                runAsync { MagickExportConfig.save() }
            }
        }
    }

    override val root = vbox {
        sceneProperty().onChangeOnce { onInitialized() }

        spacing = 10.0
        paddingAll = 10.0
        useMaxWidth = true

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Format:")
            choicebox(
                exportFormatProperty(),
                ExportFormat.REGISTRY.toList()
            ) {
                converter = object : StringConverter<ExportFormat>() {
                    override fun toString(format: ExportFormat) = format.name
                    override fun fromString(formatName: String) = ExportFormat[formatName]
                }
            }
        }
        label("Info: Postprocessing is currently not applied - you will get the raw data!")

        separator()

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Magick executable path:")
        }
        hbox(0.0, Pos.CENTER_LEFT) {
            textfield(MagickExport.magickExecutableProperty.stringBinding {
                it?.absolutePath ?: "magick (in PATH)"
            }) {
                isEditable = false
                onLeftClick { findMagickExecutable(currentWindow) }
                hgrow = Priority.SOMETIMES
                minWidth = Region.USE_COMPUTED_SIZE
            }
            button("Find executable") {
                action { findMagickExecutable(currentWindow) }
            }
        }
        button("Use magick in PATH") {
            disableProperty().bind(MagickExport.magickExecutableProperty.isNull)
            action { MagickExport.magickExecutable = null }
        }

        separator()

        hbox(10.0, Pos.CENTER_LEFT) {
            checkbox("Open after export", openAfterExportProperty()) {
                Desktop.getDesktop().apply {
                    if (!isSupported(Desktop.Action.OPEN)) {
                        openAfterExport = false
                        isDisable = true
                        Log.warn("\"Open after export\" not supported on this platform")
                        this@hbox.tooltip("Not supported on this platform")
                    }
                }
            }
        }

        // TODO: mount into renderer.setSnapshotControl

        hbox(10.0, Pos.CENTER_LEFT) {
            button("Export using Magick") {
                action { launchExport() }
                disableProperty().bind(currentlyExportingProperty())
            }
            progressindicator {
                visibleWhen(currentlyExportingProperty())
            }
        }
    }

    private fun launchExport() {
        val scene = sceneManager.scene
        if (scene.chunks.isEmpty() || scene.mode == RenderMode.PREVIEW || scene.spp == 0) {
            warning(
                header = "Nothing to export",
                content = "Cannot export the scene right now. Reasons might be:\n"
                    + "- the scene is empty\n"
                    + "- the scene was not rendered yet",
                ButtonType.OK,
                owner = currentWindow,
            )
        } else {
            runAsync<File> {
                sceneManager.taskTracker
                    .task("Exporting using Magick", -1)
                    .use {
                        MagickExport.export(
                            sceneManager.scene.facade.rgbPixelBuffer,
                            context.getSceneFile(scene.name),
                            exportFormat
                        )
                    }
            }.apply {
                setOnRunning {
                    currentlyExporting = true
                }
                setOnCancelled {
                    currentlyExporting = false
                }
                setOnFailed {
                    currentlyExporting = false
                    Log.error("Export failed", exception)
                }
                setOnSucceeded {
                    currentlyExporting = false
                    if (openAfterExport) {
                        Desktop.getDesktop().open(value)
                    }
                }
            }
        }
    }

    private fun findMagickExecutable(ownerWindow: Window?) {
        val selectedFiles = chooseFile(
            title = "Find Magick Executable",
            filters = arrayOf(
                FileChooser.ExtensionFilter("Magick Executable", "*.*")
            ),
            owner = ownerWindow
        ) {
            initialFileName = "magick"
        }
        if (selectedFiles.isNotEmpty()) {
            val file = selectedFiles.first()
            if (!Files.isExecutable(file.toPath())) {
                kotlin.error("Selected file is not executable")
            }
            MagickExport.magickExecutable = file
        }
    }

    override fun update(scene: Scene) {}
}
