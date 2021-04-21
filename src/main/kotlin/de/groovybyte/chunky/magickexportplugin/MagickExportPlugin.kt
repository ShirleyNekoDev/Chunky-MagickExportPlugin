package de.groovybyte.chunky.magickexportplugin

import de.groovybyte.chunky.magickexportplugin.magick.MagickExportFormat.*
import de.groovybyte.chunky.magickexportplugin.ui.MagickExportTab
import se.llbit.chunky.Plugin
import se.llbit.chunky.main.Chunky
import se.llbit.chunky.main.ChunkyOptions
import se.llbit.chunky.renderer.export.PictureExportFormats
import se.llbit.chunky.ui.ChunkyFx
import se.llbit.chunky.ui.render.RenderControlsTabTransformer
import se.llbit.log.Log

/**
 * @author Maximilian Stiede
 */
class MagickExportPlugin : Plugin {
    companion object {
        const val NAME = "MagickExportPlugin"
    }

    override fun attach(chunky: Chunky) {
        if (!chunky.isHeadless) {
            registerFormats()
            attachTabs(chunky)
        }
    }

    private fun attachTabs(chunky: Chunky) {
        val oldTransformer: RenderControlsTabTransformer = chunky.renderControlsTabTransformer
        chunky.renderControlsTabTransformer = RenderControlsTabTransformer { tabs ->
            oldTransformer
                .apply(tabs)
                .apply { add(MagickExportTab(chunky)) }
        }
    }

    private fun registerFormats() {
        val newFormats = arrayOf(
            EXR,
            PFM,
            PNG8,
            PNG16
        )
        Log.info("$NAME introduces these new formats: ${newFormats.joinToString { it._name }}")
        newFormats.forEach(PictureExportFormats::registerFormat)
    }
}

fun main() {
    // Start Chunky normally with this plugin attached.
    Chunky.loadDefaultTextures()
    val chunky = Chunky(ChunkyOptions.getDefaults())
    MagickExportPlugin().attach(chunky)
    ChunkyFx.startChunkyUI(chunky)
}
