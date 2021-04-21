package de.groovybyte.chunky.magickexportplugin.ui

import de.groovybyte.chunky.magickexportplugin.MagickExportConfig
import de.groovybyte.chunky.magickexportplugin.MagickExportConfig.persistentProperty
import de.groovybyte.chunky.magickexportplugin.magick.MagickExport
import de.groovybyte.chunky.magickexportplugin.magick.MagickExportFormat
import de.groovybyte.chunky.magickexportplugin.ui.chunkybindings.ChunkyAdvancedTabBinding
import de.groovybyte.chunky.magickexportplugin.utils.longName
import de.groovybyte.chunky.magickexportplugin.utils.shortName
import de.groovybyte.chunky.magickexportplugin.utils.taskTracker
import javafx.beans.binding.Bindings
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
import se.llbit.chunky.ui.RenderControlsFxController
import se.llbit.chunky.ui.render.RenderControlsTab
import se.llbit.log.Log
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.ByteOrder
import java.nio.file.Files

/**
 * @author Maximilian Stiede
 */
class MagickExportTab(
    val chunky: Chunky
) : RenderControlsTab, Fragment() {

    override fun getTabTitle(): String = "MagickExport"

    override fun getTabContent(): Node = root

    lateinit var advancedTabBinding: ChunkyAdvancedTabBinding

    fun exportFormatProperty() = getProperty(MagickExportTab::exportFormat)
    var exportFormat by property<MagickExportFormat?>()

    fun openAfterExportProperty() = getProperty(MagickExportTab::openAfterExport)
    var openAfterExport by persistentProperty("openAfterExport", true)

    fun currentlyExportingProperty() = getProperty(MagickExportTab::currentlyExporting)
    var currentlyExporting by property(false)

    private val context: RenderContext get() = chunky.renderContext
    private val sceneManager: SceneManager get() = chunky.sceneManager

    //    private val canvasSizeProperty = SimpleStringProperty("0x0")
    override fun setController(controller: RenderControlsFxController) {
        runLater {
//            sceneManager.scene?.run { "${width}x${height}" }.also { size ->
//                canvasSizeProperty.set(size)
//            }
//            tryBindingCanvasSizeProperty(canvasSizeProperty, controller)
            advancedTabBinding = ChunkyAdvancedTabBinding(controller).apply {
                bindExportFormat(exportFormatProperty())
            }
        }
    }

    private fun onInitialized() {
        MagickExportConfig.configChangedProperty.onChange {
            if (it) {
                runAsync { MagickExportConfig.save() }
            }
        }
    }

    override fun update(scene: Scene) {
        // TODO: scene changed!
    }

    override val root = vbox(10.0) {
        sceneProperty().onChangeOnce { onInitialized() }

        paddingAll = 10.0
        useMaxWidth = true

        text("Exporting using Magick does not apply postprocessing to minimize lossy operations on the raw data!")

        separator()

        hbox(10.0, Pos.CENTER_LEFT) {
            label("Magick executable path:")
        }
        hbox(0.0, Pos.CENTER_LEFT) {
            textfield(
                MagickExport.magickExecutablePathProperty
                    .stringBinding { it ?: "magick (in PATH)" }
            ) {
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
            disableProperty().bind(MagickExport.magickExecutablePathProperty.isNull)
            action { MagickExport.magickExecutablePath = null }
        }

        separator()

//        hbox(10.0, Pos.CENTER_LEFT) {
//            checkbox("Open after export", openAfterExportProperty()) {
//                Desktop.getDesktop().apply {
//                    if (!isSupported(Desktop.Action.OPEN)) {
//                        openAfterExport = false
//                        isDisable = true
//                        Log.warn("\"Open after export\" not supported on this platform")
//                        this@hbox.tooltip("Not supported on this platform")
//                    }
//                }
//            }
//        }

        // TODO: mount into renderer.setSnapshotControl

//        hbox(10.0, Pos.CENTER_LEFT) {
//            button("Export using Magick") {
//                action { launchExport() }
//                disableProperty().bind(currentlyExportingProperty())
//            }
//            progressindicator {
//                visibleWhen(currentlyExportingProperty())
//            }
//        }

        titledpane("Expert Settings", collapsible = true) {
            isAnimated = false
            isExpanded = false

            vbox(10.0) {

                vbox(10.0, Pos.CENTER_LEFT) {
                    label("Magick parameters")
                    val parametersListObservable = Bindings.concat( // TODO
                        MagickExport.magickExecutablePathProperty.stringBinding {
                            it ?: "magick"
                        },
                        " convert",
                        "\n -size WIDTHxHEIGHT",
                        "\n -depth 64",
                        "\n -define quantum:format=floating-point",
                        "\n -define quantum:minimum=0.0",
                        "\n -define quantum:maximum=1.0",
                        "\n -colorspace RGB", //sRGB
                        "\n -endian ",
                        MagickExport.endiannessProperty.stringBinding { it!!.shortName },
                        exportFormatProperty().stringBinding {
                            it?.additionalMagickParameters?.let { list ->
                                if (list.isNotEmpty()) {
                                    list.joinToString(
                                        separator = "\n ",
                                        prefix = "\n "
                                    )
                                } else ""
                            }
                        },
                        "\n RGB:-",
                        "\n ",
                        exportFormatProperty().stringBinding { it?.magickFormat },
                        ":-"
                    )
                    textarea(
                        Bindings.`when`(exportFormatProperty().isNotNull)
                            .then(parametersListObservable)
                            .otherwise(
                                "Selected export format is not using Magick.\n" +
                                "Use a magick output format to see its parameters."
                            )
                    ) {
                        isEditable = false
                        isWrapText = true
                    }
                }

                separator()

                label("Try to switch the settings here if the export looks weird")

                hbox(10.0, Pos.CENTER_LEFT) {
                    label("Endianness:")
                    choicebox(
                        MagickExport.endiannessProperty,
                        listOf(ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN)
                    ) {
                        converter = object : StringConverter<ByteOrder>() {
                            override fun toString(byteOrder: ByteOrder) =
                                "${byteOrder.longName} (${byteOrder.shortName})"

                            override fun fromString(formatName: String) = null
                        }
                    }
                }

                button("Reset to Defaults") {
                    action {
                        confirm(
                            title = "Reset expert settings?",
                            header = "Confirmation",
                            content = "Do you really want to reset the expert settings to its defaults?",
                            confirmButton = ButtonType.YES,
                            cancelButton = ButtonType.CANCEL,
                            owner = currentWindow,
                        ) {
                            MagickExport.expertSettingsResetGroup.resetAll()
                        }
                    }
                }
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
            Log.warn(
                "Export not possible, reason: ${
                    when {
                        scene.chunks.isEmpty() -> "Chunks empty"
                        scene.mode == RenderMode.PREVIEW -> "Preview mode"
                        else -> "Spp = 0"
                    }
                }"
            )
        } else {
            runAsync<File> {
                context.getSceneFile(scene.name)
                    .run {
                        resolveSibling(
                            "${nameWithoutExtension}${scene.outputMode.extension}"
                        )
                    }
                    .apply {
                        scene.outputMode.write(
                            outputStream(),
                            scene,
                            sceneManager.taskTracker
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
                }
            }
        }
    }

    private fun openExportedImage(file: File) {
        if (openAfterExport) {
            try {
                Desktop.getDesktop().open(file)
            } catch (ex: IOException) {
                // TODO: alert: could not open file
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
            MagickExport.magickExecutablePath = file.absolutePath
        }
    }
}
