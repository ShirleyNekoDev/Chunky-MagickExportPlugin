package de.groovybyte.chunky.magickexportplugin

import de.groovybyte.chunky.magickexportplugin.ui.MagickExportTab
import de.groovybyte.chunky.magickexportplugin.utils.isHeadless
import se.llbit.chunky.Plugin
import se.llbit.chunky.main.Chunky
import se.llbit.chunky.main.ChunkyOptions
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
        if (chunky.isHeadless()) {
            Log.warn("The $NAME currently does not support headless mode and will not be enabled.")
            return
        }

        attachTabs(chunky)
    }

    private fun attachTabs(chunky: Chunky) {
        val oldTransformer: RenderControlsTabTransformer = chunky.renderControlsTabTransformer
        chunky.renderControlsTabTransformer = RenderControlsTabTransformer { tabs ->
            oldTransformer
                .apply(tabs)
                .apply { add(MagickExportTab(chunky)) }
        }
    }
}

fun main() {
    // Start Chunky normally with this plugin attached.
    Chunky.loadDefaultTextures()
    val chunky = Chunky(ChunkyOptions.getDefaults())
    MagickExportPlugin().attach(chunky)
    ChunkyFx.startChunkyUI(chunky)
}
