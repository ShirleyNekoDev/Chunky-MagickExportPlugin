package de.groovybyte.chunky.magickexportplugin.chunky

import se.llbit.chunky.renderer.scene.Scene

/**
 * @author Maximilian Stiede
 */
class SceneFacade(
    val scene: Scene
) {
    val rgbPixelBuffer: RGBPixelBuffer
        get() = RGBPixelBuffer(scene.width, scene.height, scene.sampleBuffer)
}
val Scene.facade get() = SceneFacade(this)
